package swt6.issuetracker.domain;

public final class Pair<TFirst, TSecond> {
	private TFirst first;
	private TSecond second;

	public Pair(TFirst first, TSecond second) {
		this.first = first;
		this.second = second;
	}

	public TFirst getFirst() {
		return this.first;
	}

	public void setFirst(TFirst first) {
		this.first = first;
	}

	public TSecond getSecond() {
		return this.second;
	}

	public void setSecond(TSecond second) {
		this.second = second;
	}
}
