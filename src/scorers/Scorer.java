package scorers;

import java.io.IOException;
import java.util.Set;


public interface Scorer {
	public double score(Set<String> queryTerms) throws IOException;
	public String getName();
}
