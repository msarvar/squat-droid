package squat.utils;

public class Value<T> {
	private T value;
	public T get() {
		return value;
	}
	public void set(T value) {
		this.value = value;
	}
}
