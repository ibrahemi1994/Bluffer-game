package Tokenizer;

public interface TokenizerFactory<T> {
   MessageTokenizer<T> create();
}
