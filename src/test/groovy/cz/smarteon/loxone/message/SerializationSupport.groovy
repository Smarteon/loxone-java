package cz.smarteon.loxone.message

import cz.smarteon.loxone.Codec

trait SerializationSupport {

    static <T> T readResource(String path, Class<T> type) {
        def stream = SerializationSupport.class.getClassLoader().getResourceAsStream(path)
        return Codec.readMessage(stream, type)
    }

    static <T> T readResourceXml(String path, Class<T> type) {
        def stream = SerializationSupport.class.getClassLoader().getResourceAsStream(path)
        return Codec.readXml(stream, type)
    }

    static <T> T readValue(String value, Class<T> type) {
        return Codec.readMessage(value, type)
    }

    static String writeValue(def value) {
        return Codec.writeMessage(value)
    }

    static Date getDate() {
        def cal = Calendar.getInstance()
        cal.set(Calendar.MILLISECOND, 0)
        return cal.getTime()
    }

    static String formatDate(Date date) {
        return Codec.DATE_FORMAT.format(date)
    }

    static Date parseDate(String date) {
        return Codec.DATE_FORMAT.parse(date)
    }
}
