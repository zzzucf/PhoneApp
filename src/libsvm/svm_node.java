package libsvm;

public class svm_node implements java.io.Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4461330525658878835L;
	public int index;
	public double value;
	
	public svm_node()
	{
		
	}
	
	public svm_node(int index, double value)
	{
		this.index = index;
		this.value = value;
	}
}
