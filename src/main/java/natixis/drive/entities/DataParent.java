package natixis.drive.entities;

public class DataParent {
	
	private Data data;
	private Object meta;
	private Object error;

	public DataParent() {
		super();
	}

	public Data getData() {
		return data;
	}

	public void setData(Data data) {
		this.data = data;
	}

	public Object getMeta() {
		return meta;
	}

	public void setMeta(Object meta) {
		this.meta = meta;
	}

	public Object getError() {
		return error;
	}

	public void setError(Object error) {
		this.error = error;
	}

	@Override
	public String toString() {
		return "DataParent [data=" + data + ", meta=" + meta + ", error=" + error + "]";
	}

}
