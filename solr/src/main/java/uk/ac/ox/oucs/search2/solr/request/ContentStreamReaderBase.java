package uk.ac.ox.oucs.search2.solr.request;

import org.apache.commons.io.input.ReaderInputStream;
import org.apache.solr.common.util.ContentStreamBase;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

/**
 * ContentStreamReaderBase invert the default behaviour of {@link org.apache.solr.common.util.ContentStreamBase}.
 * Instead of having a {@link #getReader} method based on the result of {@link #getStream()}, {@link #getStream()} uses
 * the {@link java.io.Reader} provided by {@link #getReader()}.
 *
 * @author Colin Hebert
 */
public abstract class ContentStreamReaderBase extends ContentStreamBase {
    public abstract Reader getReader() throws IOException;

    @Override
    public InputStream getStream() throws IOException {
        String charset = getCharsetFromContentType(getContentType());
        return charset == null
                ? new ReaderInputStream(getReader(), DEFAULT_CHARSET)
                : new ReaderInputStream(getReader(), charset);
    }
}
