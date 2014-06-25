// Channel configuration - add , remove channels

package com.mitv.homepage;

import java.util.Map;
import java.util.TreeMap;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mitv.R;
import com.mitv.activities.HomeActivity;
import com.robotium.solo.Solo;
import com.robotium.solo.Solo.Config;

public class ChannelConfigTest extends ActivityInstrumentationTestCase2<HomeActivity>{
	
	private final int TIMEOUT_SMALL = 1000;
	private final int TIMEOUT_LARGE = 8000;
    private Map<String,String> tree = new TreeMap<String,String>();
	private boolean loggedin = false;

	private Solo solo;
	public ChannelConfigTest() {
		super(HomeActivity.class);
	}
	@Override
	protected void setUp() throws Exception {
		Config config = new Config();
		config.shouldScroll = true;
		config.timeout_small = TIMEOUT_SMALL;
		config.timeout_large = TIMEOUT_LARGE;
		config.useJavaScriptToClickWebElements = true;

		solo = new Solo(getInstrumentation(), getActivity());
		//getActivity();
	}
	
	@Override
    protected void tearDown() throws Exception {  
		if(loggedin) {
			logout();
		}
    	solo.finishOpenedActivities();
    }
	
// 3.2.1 - Channel configuration
	public void test_1channel_Configuration() throws Exception{

		login();
		int addedChannelsInProfile = getCountInProfile();
		int channelsInTodos = getCountInTodos();
		
		//Validating the selected channels are displaying in Todos page. Deducting 2 items from the list as it counts +Agrega canales a tu programacion and copa Mundial
		assertEquals(addedChannelsInProfile,(channelsInTodos-2));  
			
	}
	
	
	public void test_2add_channel_Configuration() throws Exception{
		login();
		addChannel();
		solo.goBack();
		solo.sleep(TIMEOUT_SMALL);
//		solo.clickOnView(solo.getView(R.id.tab_activity));
//		solo.clickOnView(solo.getView(R.id.tab_me));
		int addedChannelsInProfile = getCountInProfile();
		int channelsInTodos = getCountInTodos();
		
		//Validating the selected channels are displaying in Todos page. Deducting 2 items from the list as it counts +Agrega canales a tu programacion and copa Mundial
		assertEquals(addedChannelsInProfile,(channelsInTodos-2));  		
	}
	
	
	public void test_3remove_channel_Configuration() throws Exception{
		login();
		removeChannel();
		solo.goBack();
		solo.sleep(TIMEOUT_SMALL);
		int addedChannelsInProfile = getCountInProfile();
		int channelsInTodos = getCountInTodos();
		
		//Validating the selected channels are displaying in Todos page. Deducting 2 items from the list as it counts +Agrega canales a tu programacion and copa Mundial
		assertEquals(addedChannelsInProfile,(channelsInTodos-2));  		
	}
	
	public void addChannel(){
		solo.sleep(TIMEOUT_LARGE);
		solo.clickOnView((TextView)solo.getText("Selecciona"));     
		// verify the landing page 
		solo.waitForText("Canales seleccionados");
		assertTrue(solo.waitForView((ListView)solo.getView(R.id.listview)));
		
		ListView lv_channels = (ListView) solo.getView(R.id.listview);
//		lv.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
		
		int totalChannels_profile=lv_channels.getCount();
		 
		 int count = 0;
		 int i;
		 
		 for(count=0;count<=(totalChannels_profile/6);count++){
	 		solo.sleep(TIMEOUT_SMALL);
			ListView lv1 = (ListView) solo.getView(R.id.listview);
			RelativeLayout rowItem;
	        for (i = 0; i < lv1.getChildCount(); i++) {
	             rowItem = (RelativeLayout) lv1.getChildAt(i);
	             TextView channelNameView = (TextView) rowItem.findViewById(R.id.row_mychannels_channel_text_title_tv);
	             CharSequence cName = channelNameView.getText();
	             TextView selectionView = (TextView) rowItem.findViewById(R.id.row_mychannels_channel_button_tv);
	             CharSequence selection = selectionView.getText();
	             
	             if(selection.equals("+  Agregar"))
	            	 solo.clickOnView((TextView) rowItem.findViewById(R.id.row_mychannels_channel_button_tv));
	             
	             tree.put((String) cName,(String) selection);
//	             System.out.println("K: channel--- "+cName+" selection:--- "+selection); 
	        }
	        solo.scrollDown();
		 }
	}
	
	
	public void removeChannel(){
		solo.sleep(TIMEOUT_LARGE);
		solo.clickOnView((TextView)solo.getText("Selecciona"));     
		// verify the landing page 
		solo.waitForText("Canales seleccionados");
		assertTrue(solo.waitForView((ListView)solo.getView(R.id.listview)));
		
		ListView lv_channels = (ListView) solo.getView(R.id.listview);
//		lv.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
		
		int totalChannels_profile=lv_channels.getCount();
		 
		 int count = 0;
		 int i;
		 
		 for(count=0;count<=(totalChannels_profile/6);count++){
	 		solo.sleep(TIMEOUT_SMALL);
			ListView lv1 = (ListView) solo.getView(R.id.listview);
			RelativeLayout rowItem;
	        for (i = 0; i < lv1.getChildCount(); i++) {
	             rowItem = (RelativeLayout) lv1.getChildAt(i);
	             TextView channelNameView = (TextView) rowItem.findViewById(R.id.row_mychannels_channel_text_title_tv);
	             CharSequence cName = channelNameView.getText();
	             TextView selectionView = (TextView) rowItem.findViewById(R.id.row_mychannels_channel_button_tv);
	             CharSequence selection = selectionView.getText();
	             
	             if(selection.equals("Remover"))
	            	 solo.clickOnView((TextView) rowItem.findViewById(R.id.row_mychannels_channel_button_tv));
	             
	             tree.put((String) cName,(String) selection);
	        } 
	        solo.scrollDown(); 
		 }
	}
	
	
	public void login(){
//		solo.waitForView(solo.getView(R.id.home_indicator));
//		solo.clickOnText(java.util.regex.Pattern.quote("Mi perfil"));       
		solo.clickOnView(solo.getView(R.id.tab_me));
		
		//verify the landing page in mi perfil
		assertTrue(solo.waitForView(solo.getView(R.id.myprofile_login_container_text)));
		
		//Click the signin link Inicia sesion
		if(solo.searchText("Cerrar",true)) solo.clickOnText(java.util.regex.Pattern.quote("Cerrar"));  

		solo.clickOnText(java.util.regex.Pattern.quote("Inicia"));       

		//verify the landing page in Inicia sesion
		assertTrue(solo.waitForView(R.id.mitvlogin_login_email_edittext));
		
		// Enter the credentials and click signin button
		solo.typeText((EditText) solo.getView(R.id.mitvlogin_login_email_edittext), "k.s.karthikeyan.mitv2@gmail.com");
		solo.typeText((EditText) solo.getView(R.id.mitvlogin_login_password_edittext), "Karts007");
		solo.clickOnView(solo.getView(R.id.mitvlogin_login_button));

		// click on Seleccciona tus canales
//		solo.clickOnView(solo.getView(R.id.myprofile_channels_title_tv));
		
		assertTrue(solo.waitForView((TextView)solo.getText("Selecciona"))); 
		
		loggedin = true;

	}
	
	public void logout(){
		solo.clickOnView(solo.getView(R.id.tab_me));
		solo.clickOnText(java.util.regex.Pattern.quote("Cerrar"));  
		loggedin=false;
	}
	
	
	public int getCountInProfile(){
		solo.sleep(TIMEOUT_LARGE);
		solo.clickOnView((TextView)solo.getText("Selecciona"));     
		// verify the landing page 
		solo.waitForText("Canales seleccionados");
		assertTrue(solo.waitForView((ListView)solo.getView(R.id.listview)));
		
		ListView lv_channels = (ListView) solo.getView(R.id.listview);
//		lv.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
		
		int totalChannels_profile=lv_channels.getCount();
//		 System.out.println("KKK:count- "+totalChannels_profile);

		 
		 int count = 0;
		 int i;
		 
		
		for(count=0;count<=(totalChannels_profile/4);count++){
	 		solo.sleep(TIMEOUT_SMALL);

			ListView lv1 = (ListView) solo.getView(R.id.listview);
	
			 RelativeLayout rowItem;
	         for (i = 0; i < lv1.getChildCount(); i++) {
	             rowItem = (RelativeLayout) lv1.getChildAt(i);
	             
	             TextView channelNameView = (TextView) rowItem.findViewById(R.id.row_mychannels_channel_text_title_tv);
	             CharSequence cName = channelNameView.getText();
	             
	             TextView selectionView = (TextView) rowItem.findViewById(R.id.row_mychannels_channel_button_tv);
	             CharSequence selection = selectionView.getText();
	             
	             tree.put((String) cName,(String) selection);
	             
//	             System.out.println("K: channel--- "+cName+" selection:--- "+selection); 
	         }
	         
	        solo.scrollDown();
	        
		 }
		 
//		System.out.println("size of tree --- "+tree.size());
//		System.out.println("tree--- "+tree.toString());
		
		
//		System.out.println("treemap keyset - "+tree.keySet());
//		System.out.println("treemap values - "+tree.values());
		
		int addedChannels=0;
		for(String chan:tree.keySet()){
			
			if(tree.get(chan).equals("Remover")){
//				System.out.println("this channel is added --- "+chan);
//				System.out.println("the value--- "+tree.get(chan));
				
				addedChannels++;
			}
		}
		
		System.out.println("K:Total added channels:----  "+addedChannels);
		return addedChannels;
	}
	
	
	public int getCountInTodos(){
		solo.clickOnView(solo.getView(R.id.tab_tv_guide));
//		solo.clickOnView(solo.getView(R.id.element_tab_text_guide));
		solo.clickOnText(java.util.regex.Pattern.quote("Todos"));    
		
		ListView lv_programs = (ListView) solo.getView(R.id.tvguide_table_listview);
		int totalChannels_todos=lv_programs.getCount();
		System.out.println("KKK:Total channels in Todos- "+totalChannels_todos);
		return totalChannels_todos;
	}
}
