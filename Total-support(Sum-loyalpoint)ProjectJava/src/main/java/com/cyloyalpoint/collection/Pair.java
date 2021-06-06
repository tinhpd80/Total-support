package com.cyloyalpoint.collection;

public class Pair<A, B> {

	public final A a;
	public final B b;

	public Pair(A a, B b) {
		this.a = a;
		this.b = b;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		Pair<?, ?> tuple = (Pair<?, ?>) o;
		if (!a.equals(tuple.a))
			return false;
		return b.equals(tuple.b);
	}

	@Override
	public int hashCode() {
		int result = a.hashCode();
		result = 31 * result + b.hashCode();
		return result;
	}
}