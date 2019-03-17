package swt6.issuetracker.domain;

public final class Triple<TFirst, TSecond, TThird> {
	private TFirst first;
	private TSecond second;
	private TThird third;

	public Triple(TFirst first, TSecond second, TThird third) {
		this.first = first;
		this.second = second;
		this.third = third;
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

	public TThird getThird() {
		return this.third;
	}

	public void setThird(TThird third) {
		this.third = third;
	}
}
