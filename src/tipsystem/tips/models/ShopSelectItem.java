package tipsystem.tips.models;

public class ShopSelectItem {
	
	private String Name;
	private String IP;
	private String edate;
	private boolean isSelect;
	
	public ShopSelectItem (String name, String ip, String edate, boolean isSelect)
	{
		this.Name = name;
		this.IP = ip;
		this.setEdate(edate);
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

	public String getEdate() {
		return edate;
	}

	public void setEdate(String edate) {
		this.edate = edate;
	}
}
