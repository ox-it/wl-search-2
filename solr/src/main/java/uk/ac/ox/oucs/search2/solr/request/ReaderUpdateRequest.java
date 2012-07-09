package uk.ac.ox.oucs.search2.solr.request;

import com.google.common.io.CharStreams;
import com.google.common.io.InputSupplier;
import org.apache.solr.client.solrj.request.AbstractUpdateRequest;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.SolrInputField;
import org.apache.solr.common.util.Base64;
import org.apache.solr.common.util.ContentStream;
import org.apache.solr.common.util.DateUtil;
import org.apache.solr.common.util.XML;

import java.io.FilterReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.nio.ByteBuffer;
import java.util.*;

/**
 * Based on {@link org.apache.solr.client.solrj.request.UpdateRequest}, this UpdateRequest will look into
 * each {@link org.apache.solr.common.SolrInputDocument} for Readers them instead of using plain Strings only.
 */
public class ReaderUpdateRequest extends AbstractUpdateRequest {
    private List<SolrInputDocument> documents = null;

    public ReaderUpdateRequest() {
        super(METHOD.POST, "/update");
    }

    public ReaderUpdateRequest(String url) {
        super(METHOD.POST, url);
    }

    public ReaderUpdateRequest add(final SolrInputDocument doc) {
        if (documents == null) {
            documents = new ArrayList<SolrInputDocument>(2);
        }
        documents.add(doc);
        return this;
    }

    public ReaderUpdateRequest add(final Collection<SolrInputDocument> docs) {
        if (documents == null) {
            documents = new ArrayList<SolrInputDocument>(docs.size() + 1);
        }
        documents.addAll(docs);
        return this;
    }

    //--------------------------------------------------------------------------
    //--------------------------------------------------------------------------

    @Override
    public Collection<ContentStream> getContentStreams() throws IOException {
        Collection<ContentStream> contentStreams = new ArrayList<ContentStream>(documents.size());

        for (final SolrInputDocument doc : documents) {
            contentStreams.add(new ContentStreamReaderBase() {
                @Override
                public Reader getReader() throws IOException {
                    return addDocumentRequest(doc);
                }
            });
        }

        return contentStreams;
    }

    public Reader addDocumentRequest(final SolrInputDocument doc) throws IOException {
        final List<InputSupplier<? extends Reader>> readers = new LinkedList<InputSupplier<? extends Reader>>();
        readers.add(CharStreams.newReaderSupplier("<add>"));
        readers.add(new SimpleInputSupplier(getDocumentXml(doc)));
        readers.add(CharStreams.newReaderSupplier("</add>"));
        return CharStreams.join(readers).getInput();
    }

    //--------------------------------------------------------------------------
    //--------------------------------------------------------------------------

    public List<SolrInputDocument> getDocuments() {
        return documents;
    }

    public static Reader getDocumentXml(SolrInputDocument doc) throws IOException {
        List<InputSupplier<? extends Reader>> readers = new LinkedList<InputSupplier<? extends Reader>>();
        readers.add(CharStreams.newReaderSupplier("<doc boost=\"" + doc.getDocumentBoost() + "\">"));
        for (final SolrInputField field : doc) {
            readers.add(new SimpleInputSupplier(getFieldXml(field)));
        }
        readers.add(CharStreams.newReaderSupplier("</doc>"));

        return CharStreams.join(readers).getInput();
    }

    public static Reader getFieldXml(SolrInputField field) throws IOException {
        List<InputSupplier<? extends Reader>> readers = new LinkedList<InputSupplier<? extends Reader>>();
        float boost = field.getBoost();
        String name = field.getName();
        for (Object v : field) {
            if (v instanceof Date) {
                v = DateUtil.getThreadLocalDateFormat().format((Date) v);
            } else if (v instanceof byte[]) {
                byte[] bytes = (byte[]) v;
                v = Base64.byteArrayToBase64(bytes, 0, bytes.length);
            } else if (v instanceof ByteBuffer) {
                ByteBuffer bytes = (ByteBuffer) v;
                v = Base64.byteArrayToBase64(bytes.array(), bytes.position(), bytes.limit() - bytes.position());
            }

            if (v instanceof Reader) {
                Reader resultingReader = getFieldContentReaderXml("field", (Reader) v, "name", name, "boost", boost);
                readers.add(new SimpleInputSupplier(resultingReader));
            } else {
                StringWriter out = new StringWriter();
                XML.writeXML(out, "field", v.toString(), "name", name, "boost", boost);
                readers.add(CharStreams.newReaderSupplier(out.toString()));
            }

            // only write the boost for the first multi-valued field
            // otherwise, the used boost is the product of all the boost values
            boost = 1.0f;
        }
        return CharStreams.join(readers).getInput();
    }

    /**
     * escapes character data in reader
     */
    public static Reader getFieldContentReaderXml(String tag, Reader fieldContent, Object... attrs) throws IOException {
        List<InputSupplier<? extends Reader>> readers = new LinkedList<InputSupplier<? extends Reader>>();
        StringWriter out = new StringWriter();
        out.write('<');
        out.write(tag);
        for (int i = 0; i < attrs.length; i++) {
            out.write(' ');
            out.write(attrs[i++].toString());
            out.write('=');
            out.write('"');
            XML.escapeAttributeValue(attrs[i].toString(), out);
            out.write('"');
        }
        if (fieldContent == null) {
            out.write('/');
            out.write('>');
        } else {
            out.write('>');
            readers.add(CharStreams.newReaderSupplier(out.toString()));
            readers.add(new SimpleInputSupplier(new FilterChardataReader(fieldContent)));
            out = new StringWriter();
            out.write('<');
            out.write('/');
            out.write(tag);
            out.write('>');
        }
        readers.add(CharStreams.newReaderSupplier(out.toString()));
        return CharStreams.join(readers).getInput();
    }

    private static class SimpleInputSupplier implements InputSupplier<Reader> {
        private final Reader reader;

        private SimpleInputSupplier(Reader reader) {
            this.reader = reader;
        }

        @Override
        public Reader getInput() throws IOException {
            return reader;
        }
    }

    public static final String[] chardata_escapes =
            {"#0;", "#1;", "#2;", "#3;", "#4;", "#5;", "#6;", "#7;", "#8;", null, null, "#11;", "#12;", null, "#14;", "#15;", "#16;", "#17;", "#18;", "#19;", "#20;", "#21;", "#22;", "#23;", "#24;", "#25;", "#26;", "#27;", "#28;", "#29;", "#30;", "#31;", null, null, null, null, null, null, "&amp;", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, "&lt;", null, "&gt;"};

    private static class FilterChardataReader extends FilterReader {
        private final Deque<Character> nextChars = new ArrayDeque<Character>();

        protected FilterChardataReader(Reader in) {
            super(in);
        }

        @Override
        public int read(char[] cbuf, int off, int len) throws IOException {
            int i = 0;
            for (; i < len && off + i < cbuf.length; i++) {
                int read = read();
                if (read == -1) {
                    if (i == 0)
                        i = -1;
                    break;
                }
                cbuf[off + i] = (char) read;
            }

            return i;
        }

        @Override
        public int read() throws IOException {
            if (nextChars.isEmpty()) {
                int i = super.read();
                if (i <= 0 || i >= chardata_escapes.length || chardata_escapes[i] == null) {
                    return i;
                } else {
                    for (char c : chardata_escapes[i].toCharArray()) {
                        nextChars.push(c);
                    }
                }
            }
            return nextChars.pop();
        }
    }
}
