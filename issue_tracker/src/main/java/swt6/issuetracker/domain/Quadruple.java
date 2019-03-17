package swt6.issuetracker.domain;

public final class Quadruple<TFirst, TSecond, TThird, TFourth> {
	private TFirst first;
	private TSecond second;
	private TThird third;
	private TFourth fourth;

	public Quadruple(TFirst first, TSecond second, TThird third, TFourth fourth) {
		this.first = first;
		this.second = second;
		this.third = third;
		this.fourth = fourth;
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

	public TFourth getFourth() {
		return this.fourth;
	}

	public void setFourth(TFourth fourth) {
		this.fourth = fourth;
	}
}
