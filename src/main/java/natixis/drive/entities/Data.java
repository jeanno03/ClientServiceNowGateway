package natixis.drive.entities;

import java.util.Arrays;

public class Data {
	
	private Result[] result;

	public Data() {
		super();
	}

	public Result[] getResult() {
		return result;
	}

	public void setResult(Result[] result) {
		this.result = result;
	}

	@Override
	public String toString() {
		return "Data [Result=" + Arrays.toString(result) + "]";
	}
	
	

}
