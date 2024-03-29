package edu.uta.futureye.algebra;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import edu.uta.futureye.algebra.intf.Vector;
import edu.uta.futureye.util.FutureyeException;

public class SparseVector implements Vector {
	protected int dim = 0;
	protected double defaultValue = 0.0;
	protected Map<Integer,Double> data = 
		new HashMap<Integer,Double>();
	
	public SparseVector() {
	}
	
	public SparseVector(int dim) {
		this.dim = dim;
	}
	
	public SparseVector(int dim, double defaultValue) {
		this.dim = dim;
		this.defaultValue = defaultValue;
	}
	
	public SparseVector(double ...a) {
		if(a == null || a.length ==0) {
			throw new FutureyeException("Dim of SparseVector should be > 0!");
		} else {
			dim = a.length;
			for(int i=0; i<a.length; i++) {
				if(Double.compare(a[i], 0.0) != 0)
					data.put(i+1, a[i]);
			}
		}
	}
	
	@Override
	public void setDim(int dim) {
		this.dim = dim;
	}
	
	@Override
	public int getDim() {
		return dim;
	}

	@Override
	public void set(int index, double value) {
		if(index>dim) {
			throw new FutureyeException("index("+index+") > dim("+dim+")");
		}
		data.put(index, value);
	}
	
	@Override
	public Vector set(Vector v) {
		this.dim = v.getDim();
		if(v instanceof SparseVector) {
			SparseVector tmp = (SparseVector)v;
			this.data.clear();
			for(Entry<Integer, Double> e : tmp.data.entrySet()) {
				this.data.put(e.getKey(), e.getValue());
			}
			this.defaultValue = tmp.defaultValue;
		} else {
			for(int i=1;i<=v.getDim();i++) {
				this.set(i,v.get(i));
			}
		}
		return this;
	}
	
	@Override
	public Vector set(double a, Vector v) {
		this.dim = v.getDim();
		if(v instanceof SparseVector) {
			SparseVector tmp = (SparseVector)v;
			this.data.clear();
			for(Entry<Integer, Double> e : tmp.data.entrySet()) {
				this.data.put(e.getKey(), a*e.getValue());
			}
			this.defaultValue = a*tmp.defaultValue;
		} else {
			for(int i=1;i<=v.getDim();i++) {
				this.set(i,a*v.get(i));
			}
		}
		return this;
	}
	
	@Override
	public double get(int index) {
		if(index>dim) {
			throw new FutureyeException("index("+index+") > dim("+dim+")");
		}
		Double val = data.get(index);
		if(val == null) {
			return this.defaultValue;
		} else {
			return val;
		}
	}

	@Override
	public void add(int index,double value) {
		set(index,get(index)+value);
	}
	
	@Override
	public Vector add(Vector v) {
		if(v instanceof SparseVector) {
			SparseVector tmp = (SparseVector)v;
			for(Entry<Integer, Double> e : tmp.data.entrySet()) {
				this.add(e.getKey(), e.getValue());
			}
			this.defaultValue += tmp.defaultValue;

		} else {
			for(int i=1;i<=v.getDim();i++) {
				this.add(i,v.get(i));
			}
		}
		return this;
	}
	
	@Override
	public Vector add(double a, Vector v) {
		if(v instanceof SparseVector) {
			SparseVector tmp = (SparseVector)v;
			for(Entry<Integer, Double> e : tmp.data.entrySet()) {
				this.add(e.getKey(), a*e.getValue());
			}
			this.defaultValue += a*tmp.defaultValue;

		} else {
			for(int i=1;i<=v.getDim();i++) {
				this.add(i,a*v.get(i));
			}
		}
		return this;
	}
	
	@Override
	public Vector ax(double a) {
		for(Entry<Integer, Double> e : data.entrySet()) {
			this.set(e.getKey(), a*e.getValue());
		}
		this.defaultValue *= a;
		return this;
	}
	
	@Override
	public Vector axpy(double a, Vector y) {
		this.scale(a).add(y);
		return this;
	}

	@Override
	public Vector axMuly(double a, Vector y) {
		for(Entry<Integer, Double> e : data.entrySet()) {
			this.set(e.getKey(), a*e.getValue()*y.get(e.getKey()));
		}
		return this;
	}
	
	@Override
	public Vector axDivy(double a, Vector y) {
		for(Entry<Integer, Double> e : data.entrySet()) {
			this.set(e.getKey(), a*e.getValue()/y.get(e.getKey()));
		}
		return this;
	}
	
	@Override
	public Vector scale(double a) {
		for(Entry<Integer, Double> e : data.entrySet()) {
			this.set(e.getKey(), a*e.getValue());
		}
		this.defaultValue *= a;
		return this;
	}

	@Override
	public Vector shift(double dv) {
		for(Entry<Integer, Double> e : data.entrySet()) {
			this.set(e.getKey(), e.getValue() + dv);
		}
		this.defaultValue += dv;
		return this;
	}

	@Override
	public double norm1() {
		throw new UnsupportedOperationException();
	}

	@Override
	public double norm2() {
		return Math.sqrt(this.dot(this));
	}
	
	@Override
	public double normInf() {
		Double max = Double.MIN_VALUE;
		for(Double d : data.values()) {
			double abs = Math.abs(d);
			if(abs > max) max = abs;
		}
		return max;
	}
	
	@Override
	public double dot(Vector v2) {
		double rlt = 0.0;
		if(this.getDim() != v2.getDim()) {
			throw new FutureyeException(
					"ERROR: Vector dot product dim1="+
					this.getDim()+" != dim2="+v2.getDim());
		} else {
			for(int i=1;i<=getDim();i++) {
				rlt += this.get(i)*v2.get(i);
			}
		}
		return rlt;
	}

	@Override
	public Vector copy() {
		Vector r = new SparseVector(this.dim,this.defaultValue);
		for(Entry<Integer, Double> e : data.entrySet()) {
			r.set(e.getKey(), e.getValue());
		}
		return r;
	}	
	
	@Override
	public void clear() {
		this.dim = 0;
		this.data.clear();
	}
	
	@Override
	public void print() {
		for(int i=1;i<=dim;i++) {
			System.out.print(String.format("%8.6f", get(i))+"   ");
		}
		System.out.println();
		System.out.println();
	}
	
	public String toString() {
		return "SparseVector("+
			this.dim+
			"):N0R="+data.size();
	}
	
	/////////////////////////////////////////////////

	public Map<Integer,Double> getAll() {
		return this.data;
	}
}
