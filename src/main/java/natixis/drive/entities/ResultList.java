package natixis.drive.entities;

import java.util.List;

public class ResultList {
	
	private List<Result> results;

	public ResultList() {
		super();
	}

	public List<Result> getResults() {
		return results;
	}

	public void setResults(List<Result> results) {
		this.results = results;
	}

	@Override
	public String toString() {
		return "ResultList [results=" + results + "]";
	}
	
	

}
