enum Currency {
	EUR(2), USD(2), JPY(0), KWD(3), INR(2);
	final int minorUnits;

	Currency(int n) {
		minorUnits = n;
	}
}

final class Money {
	private final long minor;
	private final Currency currency;

	private Money(long m, Currency c) {
		if (m < 0) throw new IllegalArgumentException("Negative price");
		minor = m;
		currency = c;
	}

	static Money of(String decimal, Currency c) {
		java.math.BigDecimal x = new java.math.BigDecimal(decimal);
		x = x.setScale(c.minorUnits, java.math.RoundingMode.UNNECESSARY);
		return new Money(x.movePointRight(c.minorUnits).longValueExact(), c);
	}

	Money plus(Money o) {
		requireSame(o);
		return new Money(Math.addExact(minor, o.minor), currency);
	}

	Money times(Quantity q) {
		return new Money(Math.multiplyExact(minor, q.value), currency);
	}

	Money minus(Percentage p) {
		long result = java.math.BigDecimal.valueOf(minor)
			.multiply(java.math.BigDecimal.valueOf(10000 - p.basisPoints))
			.divide(java.math.BigDecimal.valueOf(10000), 0,
					java.math.RoundingMode.HALF_EVEN).longValueExact();
		return new Money(result, currency);
	}

	private void requireSame(Money o) {
		if (currency != o.currency)
			throw new IllegalArgumentException("Currency mismatch");
	}

	public String toString() {
		return currency + " " +
			java.math.BigDecimal.valueOf(minor, currency.minorUnits);
	}
}

final class Percentage {
	final int basisPoints;

	private Percentage(int bp) {
		basisPoints = bp;
	}

	static Percentage ofBasisPoints(int bp) {
		if (bp < 0 || bp > 10000) throw new IllegalArgumentException();
		return new Percentage(bp);
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof Percentage p && basisPoints == p.basisPoints;
	}

	@Override
	public int hashCode() {
		return Integer.hashCode(basisPoints);
	}
}

final class Quantity {
	final int value;

	private Quantity(int v) {
		value = v;
	}

	static Quantity of(int v) {
		if (v < 1) throw new IllegalArgumentException();
		return new Quantity(v);
	}
}

public class Main {
	public static void main(String[] args) {
		Money price = Money.of("100.00", Currency.INR);
		Money discounted = price.minus(Percentage.ofBasisPoints(1500));
		Money total = discounted.times(Quantity.of(2));
		System.out.println("Cart total: " + total);
	}
}