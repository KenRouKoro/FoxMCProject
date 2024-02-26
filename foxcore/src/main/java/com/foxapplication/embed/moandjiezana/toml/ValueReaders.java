package com.foxapplication.embed.moandjiezana.toml;

import java.util.concurrent.atomic.AtomicInteger;

import static com.foxapplication.embed.moandjiezana.toml.InlineTableValueReader.INLINE_TABLE_VALUE_READER;
import static com.foxapplication.embed.moandjiezana.toml.StringValueReaderWriter.STRING_VALUE_READER_WRITER;

class ValueReaders {
  
  static final ValueReaders VALUE_READERS = new ValueReaders();
  
  Object convert(String value, AtomicInteger index, Context context) {
    String substring = value.substring(index.get());
    for (ValueReader valueParser : READERS) {
      if (valueParser.canRead(substring)) {
        return valueParser.read(value, index, context);
      }
    }
    
    Results.Errors errors = new Results.Errors();
    errors.invalidValue(context.identifier.getName(), substring, context.line.get());
    return errors;
  }
  
  private ValueReaders() {}
  
  private static final ValueReader[] READERS = { 
    MultilineStringValueReader.MULTILINE_STRING_VALUE_READER, MultilineLiteralStringValueReader.MULTILINE_LITERAL_STRING_VALUE_READER, LiteralStringValueReader.LITERAL_STRING_VALUE_READER, STRING_VALUE_READER_WRITER, DateValueReaderWriter.DATE_VALUE_READER_WRITER, NumberValueReaderWriter.NUMBER_VALUE_READER_WRITER, BooleanValueReaderWriter.BOOLEAN_VALUE_READER_WRITER, ArrayValueReader.ARRAY_VALUE_READER, INLINE_TABLE_VALUE_READER
  };
}
