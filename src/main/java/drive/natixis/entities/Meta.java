package drive.natixis.entities;

import java.util.Arrays;

public class Meta {
	
private Object[] metas;

public Meta() {
	super();
}

public Meta(Object[] metas) {
	super();
	this.metas = metas;
}

public Object[] getMetas() {
	return metas;
}

public void setMetas(Object[] metas) {
	this.metas = metas;
}

@Override
public String toString() {
	return "Meta [metas=" + Arrays.toString(metas) + "]";
}


}
