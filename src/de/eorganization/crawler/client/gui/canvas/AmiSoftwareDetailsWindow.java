/**
 * 
 */
package de.eorganization.crawler.client.gui.canvas;

import com.smartgwt.client.types.RecordComponentPoolingMode;
import com.smartgwt.client.types.SummaryFunctionType;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.layout.VLayout;

import de.eorganization.crawler.client.datasources.AmiSoftwareDataSource;
import de.eorganization.crawler.client.model.Member;

/**
 * @author mugglmenzel
 * 
 */
public class AmiSoftwareDetailsWindow extends Window {

	private Member member;

	private Long amiId;

	private ListGrid softwareGrid = new ListGrid();

	/**
	 * 
	 */
	public AmiSoftwareDetailsWindow(Member member, Long amiId) {
		this.member = member;
		this.amiId = amiId;

		createWindowLayout();
	}

	private void createWindowLayout() {

		setTitle("Software Libraries on AMI " + amiId);
		setWidth(700);
		setHeight("70%");
		setShowMinimizeButton(false);
		setIsModal(true);
		setShowModalMask(true);
		centerInPage();

		ListGridField id = new ListGridField("id", "Id");
		id.setIncludeInRecordSummary(false);
		id.setSummaryFunction(SummaryFunctionType.COUNT);
		ListGridField name = new ListGridField("name", "Name");
		ListGridField version = new ListGridField("version", "Version");
		softwareGrid.setFields(id, name, version);
		softwareGrid.setDataSource(new AmiSoftwareDataSource(member != null ? member.getEmail() : null,
				amiId));
		softwareGrid.setWidth100();
		softwareGrid.setHeight100();
		softwareGrid.setAutoFetchData(true);
		softwareGrid
				.setRecordComponentPoolingMode(RecordComponentPoolingMode.RECYCLE);
		softwareGrid.setDataPageSize(10);

		VLayout windowLayout = new VLayout();
		windowLayout.setMargin(10);
		windowLayout.addMember(softwareGrid);

		addItem(windowLayout);
	}

}
