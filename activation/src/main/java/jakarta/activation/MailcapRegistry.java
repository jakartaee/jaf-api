package jakarta.activation;

import java.util.Map;

public interface MailcapRegistry {

    /**
     * Get the Map of MailcapEntries based on the MIME type.
     *
     * <p>
     * <strong>Semantics:</strong> First check for the literal mime type,
     * if that fails looks for wildcard &lt;type&gt;/\* and return that.
     * Return the list of all that hit.
     *
     * @param	mime_type	the MIME type
     * @return	the map of MailcapEntries
     */
    Map getMailcapList(String mime_type);

    /**
     * Get the Map of fallback MailcapEntries based on the MIME type.
     *
     * <p>
     * <strong>Semantics:</strong> First check for the literal mime type,
     * if that fails looks for wildcard &lt;type&gt;/\* and return that.
     * Return the list of all that hit.
     *
     * @param	mime_type	the MIME type
     * @return	the map of fallback MailcapEntries
     */
    Map getMailcapFallbackList(String mime_type);

    /**
     * Return all the MIME types known to this mailcap file.
     *
     * @return	a String array of the MIME types
     */
    String[] getMimeTypes();

    /**
     * Return all the native comands for the given MIME type.
     *
     * @param	mime_type	the MIME type
     * @return	a String array of the commands
     */
    String[] getNativeCommands(String mime_type);

    /**
     * appendToMailcap: Append to this Mailcap DB, use the mailcap
     * format:
     * Comment == "# <i>comment string</i>"
     * Entry == "mimetype;        javabeanclass"
     *
     * Example:
     * # this is a comment
     * image/gif       jaf.viewers.ImageViewer
     *
     * @param	mail_cap	the mailcap string
     */
    void appendToMailcap(String mail_cap);
}
