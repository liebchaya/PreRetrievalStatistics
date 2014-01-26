package scorers;

import java.io.IOException;
import java.util.List;

import java.util.Set;


import utils.MathUtils;

public class OperationScorers {
	
	public OperationScorers(Scorer scorer){
		m_scorer = scorer;
	}
	
	public double Avg(List<Set<String>> queryList) throws IOException{
		double sum = 0;
		for(Set<String> q:queryList)
			sum += m_scorer.score(q);
		return sum/queryList.size();
	}
	
	public double Max(List<Set<String>> queryList) throws IOException{
		double max = -Double.MAX_VALUE;
		for(Set<String> q:queryList){
			double score = m_scorer.score(q);
			if (score > max)
				max = score;
		}
		return max;
	}
	
	public double Dev(List<Set<String>> queryList) throws IOException{
		double avg = Avg(queryList);
		double sum = 0;
		for(Set<String> q:queryList)
			sum += (m_scorer.score(q)-avg);
		return Math.sqrt(sum/queryList.size());
	}
	
	public double Med(List<Set<String>> queryList) throws IOException{
		double[] scoresArray = new double[queryList.size()];
		int i=0;
		for(Set<String> q:queryList)
			scoresArray[i++] = m_scorer.score(q);
		return MathUtils.Median(scoresArray);
	}
	
	public double Sum(List<Set<String>> queryList) throws IOException{
		double sum = 0;
		for(Set<String> q:queryList)
			sum += m_scorer.score(q);
		return sum;
	}
	
	private Scorer m_scorer = null;
}
