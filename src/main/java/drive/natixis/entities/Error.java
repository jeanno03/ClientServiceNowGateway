package drive.natixis.entities;

import java.util.Arrays;

public class Error {
	
private Object[] errors;

public Error() {
	super();
}

public Error(Object[] errors) {
	super();
	this.errors = errors;
}

public Object[] getErrors() {
	return errors;
}

public void setErrors(Object[] errors) {
	this.errors = errors;
}

@Override
public String toString() {
	return "Error [errors=" + Arrays.toString(errors) + "]";
}

}
