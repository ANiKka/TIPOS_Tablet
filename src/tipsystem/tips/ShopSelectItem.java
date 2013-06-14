package tipsystem.tips;

public class ShopSelectItem {
	
	private String Name;
	private String IP;
	private boolean isSelect;
	
	public ShopSelectItem (String name, String ip, boolean isSelect)
	{
		this.Name = name;
		this.IP = ip;
		this.isSelect = isSelect;
	}
	
	public String getName()
	{
		return this.Name;
	}
	
	public String getIP()
	{
		return this.IP;
	}
	
	public boolean getIsSelect()
	{
		return this.isSelect;
	}
	
	public void SetName (String name)
	{
		this.Name = name;
	}
	
	public void SetIP (String ip)
	{
		this.IP = ip;
	}
	
	public void SetIsSelect (boolean isSelect)
	{
		this.isSelect = isSelect;
	}
	
	

}
