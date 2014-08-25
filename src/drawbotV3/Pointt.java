package drawbotV3;


import java.awt.geom.Point2D;

import drawbotV3.Pointt;
import static java.lang.Math.*;

public class Pointt
{
	public float x, y;
	
	/*
	 * CONSTRUCTORS
	 */
	public Pointt() {
	}
	public Pointt(float xx, float yy) {
		x=xx; y=yy;
	}
	public Pointt(double xx, double yy) {
		this((float) xx, (float) yy);
	}
	public Pointt(Point2D.Float p) {
		this(p.x, p.y);
	}
	public Pointt(Point2D p) {
		this( p.getX(), p.getY());
	}
	public Pointt copy() {
		return new Pointt(x,y);
	}
	public static Pointt FloatMinPointSet() {
		return new Pointt(Float.MIN_VALUE, Float.MIN_VALUE);
	}
	public static Pointt FloatMaxPointSet() {
		return new Pointt(Float.MAX_VALUE, Float.MAX_VALUE);
	}
	public static Pointt ZeroPointt() {
		return new Pointt();
	}
	/*
	 * MATH
	 */
	@Override
	public boolean equals(Object otherr) {
		if (!otherr.getClass().equals(this.getClass())) {
			return false;
		}
		Pointt other = (Pointt) otherr;
		return equals(other, .2f);
//		return Math.abs(x - other.x) < 0.2 && Math.abs(y - other.y) < 0.2;
	}
	public boolean equals(Pointt other, float epsilon) {
		return Math.abs(x - other.x) < epsilon && Math.abs(y - other.y) < epsilon;
	}
	public boolean isWithinRadius(Pointt other, float radius) {
		return radius * radius > other.minus(this).distanceSquared();
	}
	public boolean equalY(Pointt other) {
		return equalY(other, .01);
	}
	public boolean equalY(Pointt other, double epsilon) {
		return Math.abs(other.y - y) < epsilon;
	}
	public boolean equalX(Pointt other) {
		return equalX(other, .01);
	}
	public boolean equalX(Pointt other, double epsilon) {
		return Math.abs(other.x - x) < epsilon;
	}
	public boolean greaterThan(Pointt other) {
		return this.x > other.x && this.y > other.y;
	}
	public Pointt plus(Pointt other) {
		return new Pointt(other.x + x, other.y + y);
	}
	public Pointt minus(Pointt other) {
		return new Pointt(x - other.x, y - other.y);
	}
	public Pointt multiply(Pointt other) {
		return new Pointt(other.x * x, other.y * y);
	}
	public Pointt multiply(float num) {
		return new Pointt(num * x, num * y);
	}
	public Pointt dividedBy(Pointt other) {
		return new Pointt(x/other.x, y/other.y);
	}
	public Pointt dividedBy(float num) {
		if (num == 0) {
			return new Pointt(Float.MAX_VALUE, Float.MAX_VALUE);
		}
		return new Pointt(x/num, y/num);
	}
	public Pointt dividedBy(double num) {
		if (num == 0) {
			return new Pointt(Float.MAX_VALUE, Float.MAX_VALUE);
		}
		return new Pointt(x/num, y/num);
	}
	public Pointt clampToWholeNumbersWhenClose() {
		return clampToWholeNumbersWhenClose(.01f);
	}
	public Pointt clampToWholeNumbersWhenClose(float epsilon) {
		float xr = round(x);
		float yr = round(y);
		Pointt res = this.copy();
		if (abs(xr - x) < epsilon) res.x = xr; 
		if (abs(yr - y) < epsilon) res.y = yr;
		return res;
	}
	public Pointt roundPointt() {
		Pointt res = this.copy();
		res.x = (int) (res.x + .5);
		res.y = (int) (res.y + .5);
		return res;
	}
	public Pointt multiply(double num) {
		return new Pointt(num * x, num * y);
	}
	public double distance() {
		return Math.sqrt(x*x + y*y);
	}
	public double distanceSquared() {
		return x*x + y*y;
	}
	public double distanceFrom(Pointt other) {
		return this.minus(other).distance();
	}
	public double dot(Pointt other) {
		return this.x* other.x + this.y*other.y;
	}
	public Pointt unitPointt() {
		return this.dividedBy(this.distance());
	}
	public static Pointt Max(Pointt a, Pointt b) {
		if (a == null) return b;
		if (b == null) return a;
		return new Pointt(a.x > b.x ? a.x : b.x , a.y > b.y ? a.y : b.y);
	}
	public static Pointt Min(Pointt a, Pointt b) {
		if (a == null) return b;
		if (b == null) return a;
		return new Pointt(a.x < b.x ? a.x : b.x , a.y < b.y ? a.y : b.y);
	}
	
	/*
	 * Handy for swing
	 */
	public Point2D.Double point2D() {
		return new Point2D.Double(x, y);
	}
	
	public String toString() {
		return "Pointt: x: " + x + " y: " + y;
	}
	public String toStringOneDecimal() {  
		return String.format("%.1f", x)+","+String.format("%.1f", y); 
	}
}
