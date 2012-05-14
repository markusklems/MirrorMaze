package edu.kit.aifb.mirrormaze.client;

import java.util.List;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.KeyPressEvent;
import com.smartgwt.client.widgets.form.fields.events.KeyPressHandler;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.layout.Layout;
import com.smartgwt.client.widgets.layout.VLayout;

import edu.kit.aifb.mirrormaze.client.datasources.AmisDataSource;
import edu.kit.aifb.mirrormaze.client.model.Ami;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class MirrorMaze implements EntryPoint {
	/**
	 * The message displayed to the user when the server cannot be reached or
	 * returns an error.
	 */
	private static final String SERVER_ERROR = "An error occurred while "
			+ "attempting to contact the server. Please check your network "
			+ "connection and try again.";

	/**
	 * Create a remote service proxy to talk to the server-side Greeting
	 * service.
	 */
	private final MirrorMazeServiceAsync mirrorMazeService = GWT
			.create(MirrorMazeService.class);
	
	
	/**
	 * Data sources
	 */
	private final AmisDataSource amisDataSource = new AmisDataSource();

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {

		Layout masterLayout = new VLayout();
		masterLayout.setWidth100();
		masterLayout.setHeight100();

		DynamicForm s3bucket = new DynamicForm();
		
		final TextItem s3bucketName = new TextItem("s3bucketName", "S3 Bucket Name");
		s3bucketName.addKeyPressHandler(new KeyPressHandler() {
			
			@Override
			public void onKeyPress(KeyPressEvent event) {
				if(event.getKeyName().equals("Enter"))
					mirrorMazeService.importJSONFromS3(s3bucketName.getDisplayValue(), new AsyncCallback<Boolean>() {
						
						@Override
						public void onSuccess(Boolean result) {
							System.out.println("import success.");
						}
						
						@Override
						public void onFailure(Throwable caught) {
							System.out.println("import failed.");
						}
					});
			}
		});
		s3bucket.setItems(s3bucketName);
		masterLayout.addMember(s3bucket);
		
		final ListGrid amis = new ListGrid();
		amis.setWidth100();
		amis.setHeight100();
		ListGridField id = new ListGridField("id", "AMI-ID");
		ListGridField name = new ListGridField("name", "Name");
		amis.setFields(id, name);
		amis.setCanResizeFields(true);
		
		masterLayout.addMember(amis);
		mirrorMazeService.getAmis(new AsyncCallback<List<Ami>>() {

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onSuccess(List<Ami> result) {
				amisDataSource.setAmis(result);
				amis.setData(amisDataSource.createListGridRecords());
			}
		
		});
		
		//masterLayout.addMember(new UploadTestsView().getContent());
		
		masterLayout.draw();
	}
}
