package tasktracker.api;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import tasktracker.tasks.Task;

import java.io.IOException;
import java.time.LocalDateTime;

public class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {

    @Override
    public void write(JsonWriter jsonWriter, LocalDateTime localDateTime) throws IOException {
        jsonWriter.value(localDateTime.format(Task.getFormatter()));

    }

    @Override
    public LocalDateTime read(final JsonReader jsonReader) throws IOException {
        return LocalDateTime.parse(jsonReader.nextString(), Task.getFormatter());
    }
}
