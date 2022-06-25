/*
 * MIT License
 * 
 * Copyright (c) 2022 sahlaysta
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package sahlaysta.common.math;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.util.Objects;

/**
 * Immutable mathematical fraction. All fractions are simplified.
 * 
 * @author sahlaysta
 */
public class Fraction extends Number implements Comparable<Fraction>, Serializable {

	private static final long serialVersionUID = 283904956427599417L;

	// constants

	/** The fraction constant zero. */
	public static final Fraction ZERO = new Fraction(0, 1, true);

	/** The fraction constant one. */
	public static final Fraction ONE = new Fraction(1, 1, true);

	/** The fraction constant ten. */
	public static final Fraction TEN = new Fraction(10, 1, true);

	/** The fraction constant 1/2. */
	public static final Fraction ONE_HALF = new Fraction(1, 2, true);

	// fraction

	final BigInteger numer, denom;
	transient BigDecimal fval;

	// constructors

	/**
	 * Initializes a new instance of the {@link Fraction} class.
	 * 
	 * @param numerator   the fraction numerator
	 * @param denominator the fraction denominator
	 * @throws IllegalArgumentException if {@code denominator} is zero
	 */
	public Fraction(int numerator, int denominator) throws IllegalArgumentException {
		if (denominator == 0) {
			throw new IllegalArgumentException("Denominator of zero");
		}

		// negativity
		boolean neg = (numerator < 0 && denominator > 0) || (denominator < 0 && numerator > 0);
		numerator = Math.abs(numerator);
		denominator = Math.abs(denominator);

		// simplify fraction
		int gcd = gcdI(numerator, denominator);
		if (gcd != 1 && gcd != 0) {
			numerator /= gcd;
			denominator /= gcd;
		}

		numer = BigInteger.valueOf(neg ? -numerator : numerator);
		denom = BigInteger.valueOf(denominator);
	}

	/**
	 * Initializes a new instance of the {@link Fraction} class.
	 * 
	 * @param numerator   the fraction numerator
	 * @param denominator the fraction denominator
	 * @throws IllegalArgumentException if {@code denominator} is zero
	 */
	public Fraction(long numerator, long denominator) throws IllegalArgumentException {
		if (denominator == 0) {
			throw new IllegalArgumentException("Denominator of zero");
		}

		// negativity
		boolean neg = (numerator < 0 && denominator > 0) || (denominator < 0 && numerator > 0);
		numerator = Math.abs(numerator);
		denominator = Math.abs(denominator);

		// simplify fraction
		long gcd = gcdL(numerator, denominator);
		if (gcd != 1 && gcd != 0) {
			numerator /= gcd;
			denominator /= gcd;
		}

		numer = BigInteger.valueOf(neg ? -numerator : numerator);
		denom = BigInteger.valueOf(denominator);
	}

	/**
	 * Initializes a new instance of the {@link Fraction} class.
	 * 
	 * @param numerator   the fraction numerator
	 * @param denominator the fraction denominator
	 * @throws IllegalArgumentException if {@code denominator} is zero
	 * @throws NullPointerException     if {@code numerator} or {@code denominator}
	 *                                  is {@code null}
	 */
	public Fraction(BigInteger numerator, BigInteger denominator)
			throws IllegalArgumentException, NullPointerException {
		Objects.requireNonNull(numerator);
		Objects.requireNonNull(denominator);
		if (denominator.signum() == 0) {
			throw new IllegalArgumentException("Denominator of zero");
		}

		// negativity
		boolean neg = (numerator.signum() == -1 && denominator.signum() != -1)
				|| (denominator.signum() == -1 && numerator.signum() != -1);
		numerator = numerator.abs();
		denominator = denominator.abs();

		// simplify fraction
		BigInteger gcd = gcdB(numerator, denominator);
		if (gcd.signum() != 0) {
			numerator = numerator.divide(gcd);
			denominator = denominator.divide(gcd);
		}

		numer = neg ? numerator.negate() : numerator;
		denom = denominator;
	}

	/**
	 * Initializes a new instance of the {@link Fraction} class.
	 * 
	 * @param val the string representation of a fraction
	 * @throws IllegalArgumentException if {@code val} is poorly formatted, or if
	 *                                  the denominator value is zero
	 * @throws NullPointerException     if {@code val} is {@code null}
	 */
	public Fraction(String val) throws IllegalArgumentException, NullPointerException {
		this(indexOfExact(val, '/'), val);
	}

	// get index of char in string safely
	static int indexOfExact(String str, char ch) {
		int i = str.indexOf(ch);
		if (i == -1) {
			throw new IllegalArgumentException("Missing '" + ch + "' char in string.");
		}
		return i;
	}

	private Fraction(int i, String s) {
		this(new BigInteger(s.substring(0, i)), new BigInteger(s.substring(i + 1)));
	}

	// unsafe constructors

	private Fraction(BigInteger numerator, BigInteger denominator, boolean unsafe) {
		// assert unsafe;
		numer = numerator;
		denom = denominator;
	}

	private Fraction(long numerator, long denominator, boolean unsafe) {
		// assert unsafe;
		numer = BigInteger.valueOf(numerator);
		denom = BigInteger.valueOf(denominator);
	}

	// static constructors

	/**
	 * Converts a decimal number to a fraction.
	 * 
	 * @param val the decimal number
	 * @return the fraction value of the decimal number
	 * @throws NullPointerException if {@code val} is {@code null}
	 */
	public static Fraction valueOf(BigDecimal val) throws NullPointerException {
		Objects.requireNonNull(val);
		val = val.stripTrailingZeros();
		int scale = val.scale();
		BigInteger b = val.unscaledValue();
		BigInteger pow = BigInteger.TEN.pow(scale);
		return new Fraction(b, pow);
	}

	/**
	 * Converts a double to a fraction.
	 * 
	 * @param val the double value
	 * @return the fraction value of the double
	 * @throws IllegalArgumentException if {@code val} is not finite
	 */
	public static Fraction valueOf(double val) throws IllegalArgumentException {
		if (!Double.isFinite(val)) {
			throw new IllegalArgumentException("Value is not finite");
		}
		return valueOf(BigDecimal.valueOf(val));
	}

	/**
	 * Converts an integer number to a fraction.
	 * 
	 * @param val the integer number
	 * @return the fraction value of the integer number
	 * @throws NullPointerException if {@code val} is {@code null}
	 */
	public static Fraction valueOf(BigInteger val) throws NullPointerException {
		return new Fraction(Objects.requireNonNull(val), BigInteger.ONE, true);
	}

	/**
	 * Converts an long to a fraction.
	 * 
	 * @param val the long value
	 * @return the fraction value of the long
	 */
	public static Fraction valueOf(long val) {
		return valueOf(BigInteger.valueOf(val));
	}

	// find greatest common demoninator
	static int gcdI(int a, int b) {
		return b == 0 ? a : gcdI(b, a % b);
	}

	static long gcdL(long a, long b) {
		return b == 0 ? a : gcdL(b, a % b);
	}

	static BigInteger gcdB(BigInteger a, BigInteger b) {
		return b.signum() == 0 ? a : gcdB(b, a.mod(b));
	}

	/**
	 * Returns the numerator of this fraction.
	 * 
	 * @return the numerator of this fraction
	 */
	public BigInteger getNumerator() {
		return numer;
	}

	/**
	 * Returns the denominator of this fraction.
	 * 
	 * @return the denominator of this fraction
	 */
	public BigInteger getDenominator() {
		return denom;
	}

	/**
	 * Returns a fraction of value {@code (this * val)}.
	 * 
	 * @param val the fraction value
	 * @return {@code this * val}
	 * @throws NullPointerException if {@code val} is {@code null}
	 */
	public Fraction multiply(Fraction val) throws NullPointerException {
		return Objects.requireNonNull(val).signum() == 0 ? Fraction.ZERO
				: new Fraction(numer.multiply(val.numer), denom.multiply(val.denom));
	}

	/**
	 * Returns a fraction of value {@code (this + val)}.
	 * 
	 * @param val the fraction value
	 * @return {@code this + val}
	 * @throws NullPointerException if {@code val} is {@code null}
	 */
	public Fraction add(Fraction val) throws NullPointerException {
		return Objects.requireNonNull(val).signum() == 0 ? this
				: denom.compareTo(val.denom) == 0 ? new Fraction(numer.add(val.numer), denom)
						: new Fraction(numer.multiply(val.denom).add(val.numer.multiply(denom)),
								denom.multiply(val.denom));
	}

	/**
	 * Returns a fraction of value {@code (this / val)}.
	 * 
	 * @param val the fraction value
	 * @return {@code this / val}
	 * @throws ArithmeticException  if {@code val} is zero
	 * @throws NullPointerException if {@code val} is {@code null}
	 */
	public Fraction divide(Fraction val) throws ArithmeticException, NullPointerException {
		Objects.requireNonNull(val);
		if (val.signum() == 0) {
			throw new ArithmeticException("Division by zero");
		}
		return new Fraction(numer.multiply(val.denom), val.numer.multiply(denom));
	}

	/**
	 * Returns a fraction of value {@code (this - val)}.
	 * 
	 * @param val the fraction value
	 * @return {@code this - val}
	 * @throws NullPointerException if {@code val} is {@code null}
	 */
	public Fraction subtract(Fraction val) throws NullPointerException {
		return Objects.requireNonNull(val).signum() == 0 ? this
				: denom.compareTo(val.denom) == 0 ? new Fraction(numer.subtract(val.numer), denom)
						: new Fraction(numer.multiply(val.denom).subtract(val.numer.multiply(denom)),
								denom.multiply(val.denom));
	}

	/**
	 * Returns a fraction of value {@code (-this)}.
	 * 
	 * @return {@code -this}
	 */
	public Fraction negate() {
		return signum() == 0 ? this : new Fraction(numer.negate(), denom, true);
	}

	/**
	 * Returns the absolute value of this fraction.
	 * 
	 * @return {@code abs(this)}
	 */
	public Fraction abs() {
		return signum() >= 0 ? this : negate();
	}

	/**
	 * Compares this fraction with the specified fraction.
	 * 
	 * @param val the fraction value
	 * @return -1, 0 or 1 as this BigInteger is numerically less than, equal to, or
	 *         greater than {@code val}
	 * @throws NullPointerException if {@code val} is {@code null}
	 */
	@Override
	public int compareTo(Fraction val) throws NullPointerException {
		if (val == this)
			return 0;
		Objects.requireNonNull(val);

		// signum compare
		int sig = signum();
		int osig = val.signum();
		if (sig > osig)
			return 1;
		if (sig < osig)
			return -1;

		// fraction compare
		BigInteger b1 = numer.multiply(val.denom);
		BigInteger b2 = val.numer.multiply(denom);
		return b1.compareTo(b2);
	}

	// approximate fraction value (if it has repeating decimals)

	BigDecimal getfval() {
		return fval == null ? fval = new BigDecimal(numer).divide(new BigDecimal(denom), MathContext.DECIMAL128) : fval;
	}

	/**
	 * Converts this fraction to a double.
	 * 
	 * @return the double value of this fraction
	 */
	@Override
	public double doubleValue() {
		return getfval().doubleValue();
	}

	/**
	 * Converts this fraction to a float.
	 * 
	 * @return the float value of this fraction
	 */
	@Override
	public float floatValue() {
		return getfval().floatValue();
	}

	/**
	 * Converts this fraction to an int.
	 * 
	 * @return the int value of this fraction
	 */
	@Override
	public int intValue() {
		return getfval().intValue();
	}

	/**
	 * Converts this fraction to a long.
	 * 
	 * @return the long value of this fraction
	 */
	@Override
	public long longValue() {
		return getfval().longValue();
	}

	/**
	 * Returns the hash code for this fraction, computed as:
	 * 
	 * <code><pre>(this.getNumerator().hashCode() ^ this.getDenominator().hashCode())</pre></code>
	 * 
	 * @return the hash code for this fraction
	 */
	@Override
	public int hashCode() {
		return numer.hashCode() ^ denom.hashCode();
	}

	/**
	 * Returns the signum function of this fraction.
	 * 
	 * @return -1, 0 or 1 as the value of this fraction is negative, zero or
	 *         positive
	 */
	public int signum() {
		return numer.signum();
	}

	/**
	 * Compares this fraction with the specified object.
	 * 
	 * @param obj the object to compare with
	 * @return {@code true} if {@code obj} is a fraction with an equal value to this
	 *         fraction
	 */
	@Override
	public boolean equals(Object obj) {
		return obj == this ? true : obj instanceof Fraction && compareTo((Fraction) obj) == 0;
	}

	/**
	 * Returns the string representation for this fraction, computed as:
	 * 
	 * <code><pre>(this.getNumerator().toString() + '/' this.getDenominator().toString())</pre></code>
	 * 
	 * @return the string representation for this fraction
	 */
	@Override
	public String toString() {
		return numer.toString() + '/' + denom.toString();
	}
}