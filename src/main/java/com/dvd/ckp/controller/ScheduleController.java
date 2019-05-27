/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dvd.ckp.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.zkoss.util.media.Media;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.A;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Doublebox;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;

import com.dvd.ckp.business.service.ScheduleService;
import com.dvd.ckp.business.service.StaffServices;
import com.dvd.ckp.domain.Quota;
import com.dvd.ckp.domain.Schedule;
import com.dvd.ckp.domain.Staff;
import com.dvd.ckp.excel.ExcelReader;
import com.dvd.ckp.excel.ExcelWriter;
import com.dvd.ckp.excel.exception.EmptyCellException;
import com.dvd.ckp.excel.exception.InvalidCellValueException;
import com.dvd.ckp.utils.Constants;
import com.dvd.ckp.utils.DateTimeUtils;
import com.dvd.ckp.utils.FileUtils;
import com.dvd.ckp.utils.SpringConstant;
import com.dvd.ckp.utils.StringUtils;
import com.dvd.ckp.utils.StyleUtils;
import com.google.gson.Gson;

/**
 *
 * @author viettx
 * @since 01/06/2018
 * @version 1.0
 */
public class ScheduleController extends GenericForwardComposer<Component> {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(ScheduleController.class);
	@WireVariable
	protected StaffServices staffService;

	@WireVariable
	protected ScheduleService scheduleService;

	@Wire
	private Grid gridSchedule;
	@Wire
	private Combobox cbFilterName;
	@Wire
	private Textbox txtFilterClass;
	@Wire
	private Datebox dtFromDate;
	@Wire
	private Datebox dtToDate;

	@Wire
	private Datebox year;

	@Wire
	private Checkbox show;
	ListModelList<Staff> listDataStaff;

	private ListModelList<Schedule> listDataModel;
	private List<Schedule> lstSchedule;
	private List<Schedule> lstScheduleFilter;
	private int insertOrUpdate = 0;

	private static final String SAVE_PATH = "/Schedule/";
	private static final String EXPORT_FILE = "schedule_data.xlsx";

	private static final String IMPORT_PATH = "file/template/import/import_schedule.xlsx";

	private static final String ERROR_PATH = "file/template/error/error_schedule_data.xlsx";
	private static final String ERROR_PATH_TEMP = "file/template/error/temp/";
	private static final String ERROR_FILE_NAME = "Danhsachloi.xlsx";
	@Wire
	private Label linkFileName;
	@Wire
	private Textbox hiddenFileName;
	@Wire
	private Textbox hdFileName;
	@Wire
	private A btnSave;
	@Wire
	private A btnCancel;
	@Wire
	private Label title;
	@Wire
	private A linkError;

	@Wire
	private A btnDeleteData;

	private List<Staff> listStaff;

	List<Schedule> listImport;
	List<Schedule> listError;
	List<Schedule> listSucces;
	private boolean isShow = false;

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		lstScheduleFilter = new ArrayList<>();

		staffService = (StaffServices) SpringUtil.getBean(SpringConstant.STAFF_SERVICES);
		scheduleService = (ScheduleService) SpringUtil.getBean(SpringConstant.SCHEDULE_SERVICES);

		lstSchedule = new ArrayList<>();
		listStaff = staffService.getAllData();
		listDataStaff = new ListModelList<>(listStaff);
		cbFilterName.setModel(listDataStaff);
		listImport = new ArrayList<>();
		listError = new ArrayList<>();
		listSucces = new ArrayList<>();

		List<Schedule> vlstData = new ArrayList<>();
		listDataModel = new ListModelList<>(vlstData);
		gridSchedule.setModel(listDataModel);

	}

	/**
	 * Edit row
	 *
	 * @param event
	 */
	public void onEdit(ForwardEvent event) {
		Row rowSelected = (Row) event.getOrigin().getTarget().getParent().getParent();
		List<Component> lstCell = rowSelected.getChildren();
		StyleUtils.setEnableComponent(lstCell, 4);
	}

	/**
	 * 
	 * @param event
	 */
	public void onDelete(ForwardEvent event) {
		Messagebox.show(Labels.getLabel("message.confirm.delete.content"),
				Labels.getLabel("message.confirm.delete.title"), Messagebox.YES | Messagebox.NO, Messagebox.QUESTION,
				(Event e) -> {
					if (Messagebox.ON_YES.equals(e.getName())) {
						Row rowSelected = (Row) event.getOrigin().getTarget().getParent().getParent();
						List<Component> lstCell = rowSelected.getChildren();
						Schedule c = rowSelected.getValue();
						c.setCreateDate(new Date());
						if (isShow) {
							scheduleService.delete(c);
							lstScheduleFilter.remove(rowSelected.getIndex());
						} else {
							listSucces.remove(rowSelected.getIndex());
						}

						StyleUtils.setDisableComponent(lstCell, 4);
						reloadGrid();
					}
				});

	}

	/**
	 * Cancel
	 *
	 * @param event
	 */
	public void onCancel(ForwardEvent event) {
		Row rowSelected = (Row) event.getOrigin().getTarget().getParent().getParent();
		List<Component> lstCell = rowSelected.getChildren();
		StyleUtils.setDisableComponent(lstCell, 4);
		reloadGrid();
	}

	/**
	 * Xoa tat ca du lieu dang co tren he thong
	 * 
	 * @param event
	 */
	public void onDeleteData(ForwardEvent event) {
		Messagebox.show(Labels.getLabel("schedule.quota.delete.comfirm"), Labels.getLabel("comfirm"),
				Messagebox.YES | Messagebox.NO, Messagebox.QUESTION, (Event e) -> {
					if (Messagebox.ON_YES.equals(e.getName())) {
						if (lstScheduleFilter != null && !lstScheduleFilter.isEmpty()) {
							scheduleService.delete(lstScheduleFilter);
							reloadGrid();
						}
					}
				});
//        Row rowSelected = (Row) event.getOrigin().getTarget().getParent().getParent();
//        List<Component> lstCell = rowSelected.getChildren();
//
//        StyleUtils.setDisableComponent(lstCell, 4);

	}

	/**
	 *
	 * @param event
	 */
	public void onSave(ForwardEvent event) {
		Row rowSelected = (Row) event.getOrigin().getTarget().getParent().getParent();
		List<Component> lstCell = rowSelected.getChildren();
		Schedule c = rowSelected.getValue();
		Schedule schedule = getDataInRow(lstCell);
		schedule.setCreateDate(new Date());
		schedule.setId(c.getId());
		if (isShow) {
			if (insertOrUpdate == 1) {
				scheduleService.save(schedule);
				lstSchedule.add(schedule);
			} else {
				schedule.setCreateDate(new Date());
				scheduleService.save(schedule);
			}
		} else {
			if (insertOrUpdate == 1) {
				listSucces.add(schedule);
			} else {
				listSucces.set(rowSelected.getIndex(), schedule);
			}
		}
		StyleUtils.setDisableComponent(lstCell, 4);
		reloadGrid();
		insertOrUpdate = 0;
	}

	/**
	 * Add row
	 *
	 * @param event
	 */
	public void onAdd(ForwardEvent event) {
		Schedule newItem = new Schedule();
		listDataModel.add(0, newItem);
		gridSchedule.setActivePage(com.dvd.ckp.utils.Constants.FIRST_INDEX);
		gridSchedule.setModel(listDataModel);
		gridSchedule.renderAll();
		List<Component> lstCell = gridSchedule.getRows().getChildren().get(0).getChildren();
		StyleUtils.setEnableComponent(lstCell, 4);
		insertOrUpdate = 1;
		setDataDefaultInGrid();
	}

	/**
	 * Get object customer
	 *
	 * @param lstCell
	 * @return
	 */
	private Schedule getDataInRow(List<Component> lstCell) {
		Schedule schedule = new Schedule();
		Textbox txtDay = (Textbox) lstCell.get(1).getFirstChild();
		Textbox txtClass = (Textbox) lstCell.get(2).getFirstChild();
		Textbox txtThematic = (Textbox) lstCell.get(3).getFirstChild();
		Intbox txtLession = (Intbox) lstCell.get(4).getFirstChild();
		Doublebox txtCoefficient = (Doublebox) lstCell.get(5).getFirstChild();
		Combobox cbStaff = (Combobox) lstCell.get(7).getFirstChild();

		Date value = DateTimeUtils.convertStringToTime(DateTimeUtils.formatDate(txtDay.getValue().trim()),
				Constants.FORMAT_DATE);
		schedule.setFromDate(txtDay.getValue().trim());
		schedule.setDay(DateTimeUtils.getDay(value));
		schedule.setMonth(String.valueOf(DateTimeUtils.getTime(value, 2)));
		schedule.setYear(String.valueOf(DateTimeUtils.getTime(value, 3)));
		schedule.setFromDateValue(value);
		schedule.setThematic(txtThematic.getValue());
		schedule.setClassValue(txtClass.getValue());
		schedule.setLesson(txtLession.getValue().doubleValue());
		schedule.setCoefficient(txtCoefficient.getValue());
		schedule.setStaffCode(cbStaff.getSelectedItem().getValue());
		schedule.setGroup(1);
		return schedule;
	}

	/**
	 * Reload grid
	 */
	private void reloadGrid() {
		List<Schedule> vlstData = new ArrayList<>();
		lstScheduleFilter.clear();
		lstSchedule.clear();
		if (isShow) {
			Schedule schedule = new Schedule();
			String staffCode;
			if (cbFilterName.getSelectedItem() != null) {
				staffCode = cbFilterName.getSelectedItem().getValue();
				schedule.setStaffCode(staffCode);
			}
			if (dtFromDate.getValue() != null) {
				schedule.setFromDateValue(dtFromDate.getValue());
			}
			if (dtToDate.getValue() != null) {
				schedule.setToDateValue(dtToDate.getValue());
			}
			if (StringUtils.isValidString(txtFilterClass.getValue())) {
				schedule.setClassValue(txtFilterClass.getValue());
			}
			List<Schedule> list = scheduleService.onSearch(schedule);
			if (list != null && !list.isEmpty()) {
				list.forEach((s) -> {
					s.setDayValue(
							DateTimeUtils.convertDateToString(s.getFromDateValue(), Constants.FORMAT_DATE_DD_MM_YYY));
				});
				vlstData.addAll(list);
				lstSchedule.addAll(list);
				lstScheduleFilter.addAll(list);
			}
		} else {
			if (listSucces != null && !listSucces.isEmpty()) {
				vlstData.addAll(listSucces);
			}
			lstScheduleFilter.addAll(listSucces);
		}
		Collections.sort(vlstData, (Schedule s1, Schedule s2) -> s1.getFromDateValue().compareTo(s2.getFromDateValue()) // -1
		// descending
		);
		listDataModel = new ListModelList<>(vlstData);
		gridSchedule.setModel(listDataModel);
		setDataDefaultInGrid();
	}

	/**
	 *
	 */
	public void onChange$cbFilterName() {
		Schedule schedule = new Schedule();
		String staffCode;
		if (cbFilterName.getSelectedItem() != null) {
			staffCode = cbFilterName.getSelectedItem().getValue();
			schedule.setStaffCode(staffCode);
		}
		if (dtFromDate.getValue() != null) {
			schedule.setFromDateValue(dtFromDate.getValue());
		}
		if (dtToDate.getValue() != null) {
			schedule.setToDateValue(dtToDate.getValue());
		}
		if (StringUtils.isValidString(txtFilterClass.getValue())) {
			schedule.setClassValue(txtFilterClass.getValue());
		}
		filter(schedule);
	}

	/**
	 *
	 */
	public void onChange$dtToDate() {
		Schedule schedule = new Schedule();
		String staffCode;
		if (cbFilterName.getSelectedItem() != null) {
			staffCode = cbFilterName.getSelectedItem().getValue();
			schedule.setStaffCode(staffCode);
		}
		if (dtFromDate.getValue() != null) {
			schedule.setFromDateValue(dtFromDate.getValue());
		}
		if (dtToDate.getValue() != null) {
			schedule.setToDateValue(dtToDate.getValue());
		}
		if (StringUtils.isValidString(txtFilterClass.getValue())) {
			schedule.setClassValue(txtFilterClass.getValue());
		}
		filter(schedule);
	}

	/**
	 *
	 */
	public void onChange$dtFromDate() {
		Schedule schedule = new Schedule();
		String staffCode;
		if (cbFilterName.getSelectedItem() != null) {
			staffCode = cbFilterName.getSelectedItem().getValue();
			schedule.setStaffCode(staffCode);
		}
		if (dtFromDate.getValue() != null) {
			schedule.setFromDateValue(dtFromDate.getValue());
		}
		if (dtToDate.getValue() != null) {
			schedule.setToDateValue(dtToDate.getValue());
		}
		if (StringUtils.isValidString(txtFilterClass.getValue())) {
			schedule.setClassValue(txtFilterClass.getValue());
		}
		filter(schedule);
	}

	/**
	 *
	 */
	public void onChange$txtFilterClass() {
		Schedule schedule = new Schedule();
		String staffCode;
		if (cbFilterName.getSelectedItem() != null) {
			staffCode = cbFilterName.getSelectedItem().getValue();
			schedule.setStaffCode(staffCode);
		}
		if (dtFromDate.getValue() != null) {
			schedule.setFromDateValue(dtFromDate.getValue());
		}
		if (dtToDate.getValue() != null) {
			schedule.setToDateValue(dtToDate.getValue());
		}
		if (StringUtils.isValidString(txtFilterClass.getValue())) {
			schedule.setClassValue(txtFilterClass.getValue());
		}
		filter(schedule);
	}

	/**
	 *
	 * @param schedule
	 */
	private void filter(Schedule schedule) {
		logger.info("Param: " + new Gson().toJson(schedule));
		List<Schedule> vlstData = new ArrayList<>();
		List<Schedule> lst = new ArrayList<>();
		lstScheduleFilter.clear();
		vlstData.clear();
		logger.info(lstScheduleFilter.size());
		if (isShow) {
			// lst.addAll(lstSchedule);
			lst = scheduleService.onSearch(schedule);
			if (lst != null && !lst.isEmpty()) {
				vlstData.addAll(lst);
				lstScheduleFilter.addAll(lst);
			}
			logger.info("Search: " + lstScheduleFilter.size());
		} else {
			lst.addAll(listImport);
			if (!StringUtils.isValidString(schedule.getStaffCode())) {
				vlstData.addAll(lst);
				lstScheduleFilter.addAll(lst);
			} else {
				if (!lst.isEmpty()) {
					for (Schedule item : lst) {
						if (StringUtils.isValidString(schedule.getStaffCode())
								&& schedule.getStaffCode().equals(item.getStaffCode())) {
							vlstData.add(item);
							lstScheduleFilter.add(item);
						}
						if (schedule.getFromDateValue() != null
								&& schedule.getFromDateValue().compareTo(item.getFromDateValue()) == 0) {
							vlstData.add(item);
							lstScheduleFilter.add(item);
						}
						if (schedule.getFromDateValue() != null && schedule.getToDateValue() != null
								&& schedule.getFromDateValue().after(item.getFromDateValue())
								&& schedule.getFromDateValue().before(item.getFromDateValue())) {
							vlstData.add(item);
							lstScheduleFilter.add(item);
						}
						if (StringUtils.isValidString(schedule.getClassValue())
								&& schedule.getClassValue().contains(item.getClassValue())) {
							vlstData.add(item);
							lstScheduleFilter.add(item);
						}
					}
				}
			}
		}
		vlstData.forEach((s) -> {
			s.setDayValue(DateTimeUtils.convertDateToString(s.getFromDateValue(), Constants.FORMAT_DATE_DD_MM_YYY));
		});
		listDataModel = new ListModelList<>(vlstData);
		gridSchedule.setModel(listDataModel);
		setDataDefaultInGrid();
	}

	/**
	 *
	 * @param event
	 */
	public void onCancelUpload(ForwardEvent event) {
		cancel();
	}

	/**
	 *
	 */
	void cancel() {
		List<Schedule> vlstData = new ArrayList<>();
		listDataModel = new ListModelList<>(vlstData);
		gridSchedule.setModel(listDataModel);
		setDataDefaultInGrid();
		btnCancel.setVisible(false);
		btnSave.setVisible(false);
		title.setVisible(false);
		linkError.setLabel("");
		linkFileName.setValue("");
	}

	/**
	 *
	 * @param event
	 */
	public void onExport(ForwardEvent event) {
		ExcelWriter<Schedule> excelWriter = new ExcelWriter<>();
		try {
			int index = 0;
			for (Schedule item : lstScheduleFilter) {
				index++;
				item.setIndex(index);
				item.setStaffName(getStaffName(item.getStaffCode()));
			}
			String pathFileInput = session.getWebApp().getRealPath("file/template/export/") + EXPORT_FILE;
			String pathFileOut = session.getWebApp().getRealPath("file/export/") + EXPORT_FILE;
			Map<String, Object> beans = new HashMap<>();
			beans.put("data", lstScheduleFilter);
			beans.put("day", DateTimeUtils.getTime(new Date(), 1));
			beans.put("month", DateTimeUtils.getTime(new Date(), 2));
			beans.put("year", DateTimeUtils.getTime(new Date(), 3));
			excelWriter.write(beans, pathFileInput, pathFileOut);
			File file = new File(pathFileOut);
			Filedownload.save(file, null);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage(), e);
		}

	}

	/**
	 *
	 * @param event
	 */
	public void onExportError(ForwardEvent event) {
		ExcelWriter<Quota> excelWriter = new ExcelWriter<>();
		try {
			int index = 0;
			for (Schedule item : listError) {
				index++;
				item.setIndex(index);
				item.setStaffName(getStaffName(item.getStaffCode()));
			}
			String pathFileInput = session.getWebApp().getRealPath(ERROR_PATH);
			String pathFileOut = session.getWebApp().getRealPath(ERROR_PATH_TEMP);
			excelWriter.setFileOutName(ERROR_FILE_NAME);
			Map<String, Object> beans = new HashMap<>();
			beans.put("data", listError);
			beans.put("day", DateTimeUtils.getTime(new Date(), 1));
			beans.put("month", DateTimeUtils.getTime(new Date(), 2));
			beans.put("year", DateTimeUtils.getTime(new Date(), 3));
			excelWriter.write(beans, pathFileInput, pathFileOut);
			File file = new File(excelWriter.getFileExport());
			Filedownload.save(file, null);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage(), e);
		}

	}

	/**
	 *
	 * @param staffCode
	 * @return
	 */
	String getStaffName(String staffCode) {
		if (listStaff != null && !listStaff.isEmpty()) {
			for (Staff item : listStaff) {
				if (StringUtils.isValidString(staffCode) && staffCode.equals(item.getStaffCode())) {
					return item.getStaffName();
				}
			}
		}
		return "";
	}

	/**
	 *
	 * @param evt
	 */
	public void onUpload$uploadbtn(UploadEvent evt) {
		Media media = evt.getMedia();

		if (media == null) {
			Messagebox.show(Labels.getLabel("uploadExcel.selectFile"), Labels.getLabel("ERROR"), Messagebox.OK,
					Messagebox.ERROR);
			return;
		}
		show.setChecked(false);
		final String vstrFileName = media.getName();
		try {

			FileUtils fileUtils = new FileUtils();
			fileUtils.setKey(SAVE_PATH);
			fileUtils.saveFile(media, session);
			hdFileName.setValue(vstrFileName);
			linkFileName.setValue(vstrFileName);
			hiddenFileName.setValue(fileUtils.getFilePathOutput());
			ExcelReader<Schedule> reader = new ExcelReader<>();
			Object objClass = reader.readCell(fileUtils.getFilePathOutput(), 5, 1);
			List<Schedule> listData = reader.read(fileUtils.getFilePathOutput(), Schedule.class);
			List<Schedule> listDataSub = new ArrayList<>();
			List<Schedule> listIndex = new ArrayList<>();
			listImport.clear();
			if (listData != null && !listData.isEmpty()) {
				for (Schedule schedule : listData) {
					String arr[] = schedule.getFromDate().split("-");
					schedule.setClassValue(objClass.toString());
					if (arr.length == 1) {
						String array[] = arr[0].trim().split("/");
						schedule.setDayValue(StringUtils.format(array[0]) + "/" + StringUtils.format(array[1]) + "/"
								+ StringUtils.format(array[2]));
						schedule.setDay(StringUtils.format(array[0]));
						schedule.setYear(StringUtils.format(array[2]));
						schedule.setMonth(StringUtils.format(array[1]));
						schedule.setClassValue(objClass.toString());
						schedule.setCreateDate(new Date());
						String strFromDate = schedule.getFromDate().replace("S", "").replace("C", "").replace("T", "");
						array = strFromDate.split("/");

						strFromDate = array[2] + array[1] + array[0];
						Date fromDate = DateTimeUtils.convertStringToTime(strFromDate, Constants.FORMAT_DATE);
						schedule.setFromDateValue(fromDate);
						schedule.setGroup(1);
					} else {
						String strFromDate = arr[0].trim().replace("S", "").replace("C", "").replace("T", "");
						String arrayFromDate[] = strFromDate.split("/");
						strFromDate = arrayFromDate[2] + arrayFromDate[1] + arrayFromDate[0];

						String strToDate = arr[1].trim().replace("S", "").replace("C", "").replace("T", "");
						String arrayToDate[] = strToDate.split("/");
						strToDate = arrayToDate[2] + arrayToDate[1] + arrayToDate[0];

						List<Date> listDate = DateTimeUtils.getAllDay(strFromDate, strToDate);
						if (listDate != null && !listData.isEmpty()) {
							listDate.stream().map((date) -> {
								Schedule s = new Schedule();
								s.setClassValue(schedule.getClassValue());
								s.setCoefficient(schedule.getCoefficient());
								s.setCreateDate(new Date());
								s.setLesson(schedule.getLesson() / listDate.size());
								s.setStaffCode(schedule.getStaffCode());
								s.setStaffName(schedule.getStaffName());
								s.setThematic(schedule.getThematic());
								s.setDay(String.valueOf(DateTimeUtils.getTime(date, 1)) + "/"
										+ String.valueOf(DateTimeUtils.getTime(date, 2) + "/")
										+ String.valueOf(DateTimeUtils.getTime(date, 3)));
								s.setMonth(String.valueOf(DateTimeUtils.getTime(date, 2)));
								s.setYear(String.valueOf(DateTimeUtils.getTime(date, 3)));
								s.setFromDate(DateTimeUtils.convertDateToString(date, Constants.FORMAT_DATE_DD_MM_YYY));
								s.setFromDateValue(date);
								s.setGroup(1);
								return s;
							}).forEachOrdered((s) -> {
								listDataSub.add(s);
							});
						}
						listIndex.add(schedule);
					}

				}
				listIndex.forEach((schedule) -> {
					listData.remove(schedule);
				});
				if (!listDataSub.isEmpty()) {
					listData.addAll(listDataSub);
				}
			}
			listImport.addAll(listData);
			validate();
			Collections.sort(listSucces, new Comparator<Schedule>() {
				@Override
				public int compare(Schedule s1, Schedule s2) {
					// -1 - less than, 1 - greater than, 0 - equal, all inversed for descending
					return s1.getFromDateValue().compareTo(s2.getFromDateValue());
				}
			});
			listDataModel = new ListModelList<>(listSucces);
			gridSchedule.setModel(listDataModel);
			btnCancel.setVisible(true);
			btnSave.setVisible(true);
			setDataDefaultInGrid();
			lstScheduleFilter.clear();
			lstScheduleFilter.addAll(listSucces);
		} catch (InvalidCellValueException e) {
			logger.error(e.getMessage(), e);
			Messagebox.show(
					Labels.getLabel("empty.cell", new String[] { e.getColumnName(), String.valueOf(e.getRow()) }),
					Labels.getLabel("comfirm"), Messagebox.OK, Messagebox.ERROR);
			cancel();
		} catch (EmptyCellException e) {
			logger.error(e.getMessage(), e);
			Messagebox.show(
					Labels.getLabel("invalid.cell", new String[] { e.getColumnName(), String.valueOf(e.getRow()) }),
					Labels.getLabel("comfirm"), Messagebox.OK, Messagebox.ERROR);
			cancel();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			Messagebox.show(Labels.getLabel("error.cell"), Labels.getLabel("error"), Messagebox.OK, Messagebox.ERROR);
			cancel();
		}
	}

	/**
	 *
	 */
	void validate() {
		int numberError;
		listError.clear();
		listSucces.clear();
		if (listImport != null && !listImport.isEmpty()) {
			for (Schedule schedule : listImport) {
				if (!StringUtils.isValidString(schedule.getThematic())) {
					schedule.setError(Labels.getLabel("schedule.error.thematic"));
					listError.add(schedule);
					continue;
				}
				if (schedule.getLesson() == null) {
					schedule.setError(Labels.getLabel("schedule.error.lesson"));
					listError.add(schedule);
					continue;
				}
				if (!StringUtils.isValidString(schedule.getFromDate())) {
					schedule.setError(Labels.getLabel("schedule.error.date"));
					listError.add(schedule);
					continue;
				}
				if (!StringUtils.isValidString(schedule.getStaffName())) {
					schedule.setError(Labels.getLabel("schedule.error.staff.name"));
					listError.add(schedule);
					continue;
				}
				if (!StringUtils.isValidString(schedule.getStaffCode())) {
					schedule.setError(Labels.getLabel("schedule.error.staff.code"));
					listError.add(schedule);
					continue;
				}
				if (!isExistStaffCode(schedule.getStaffCode())) {
					schedule.setError("Giảng viên " + schedule.getStaffCode() + " chưa được khai báo trên hệ thống");
					listError.add(schedule);
					continue;
				}
				if (schedule.getCoefficient() == null) {
					schedule.setError(Labels.getLabel("schedule.error.coefficient"));
					listError.add(schedule);
					continue;
				}
				listSucces.add(schedule);
			}
			numberError = listError.size();
			if (numberError != 0) {
				title.setVisible(true);
				title.setValue(Labels.getLabel("schedule.error.title", new String[] { "" + numberError }));
				linkError.setVisible(true);
				linkError.setLabel(ERROR_FILE_NAME);
			}
		}
	}

	/**
	 *
	 * @param staffCode
	 * @return
	 */
	boolean isExistStaffCode(String staffCode) {
		if (listStaff != null && !listStaff.isEmpty()) {
			if (listStaff.stream().anyMatch((staff) -> (staffCode.equalsIgnoreCase(staff.getStaffCode())))) {
				return true;
			}
		}
		return false;
	}

	public void onSaveImport(ForwardEvent event) {
		Messagebox.show(Labels.getLabel("schedule.quota.upload.comfirm"), Labels.getLabel("comfirm"),
				Messagebox.YES | Messagebox.NO, Messagebox.QUESTION, (Event e) -> {
					if (Messagebox.ON_YES.equals(e.getName())) {
						try {
							Schedule item = new Schedule();
							List<Schedule> listData = scheduleService.onSearch(item);
							List<Schedule> lstUpdate = new ArrayList<>();
							List<Schedule> lstImport = new ArrayList<>();
							if (listSucces != null && !listSucces.isEmpty()) {
								listSucces.forEach((s) -> {
									Schedule schedule = isExist(listData, s);

									if (schedule == null) {
										lstImport.add(s);
									} else {
										schedule.setThematic(s.getThematic());
										schedule.setLesson(s.getLesson());
										schedule.setCoefficient(s.getCoefficient());
										schedule.setFromDate(s.getFromDate());
										schedule.setFromDateValue(s.getFromDateValue());
										schedule.setToDate(s.getToDate());
										schedule.setToDateValue(s.getToDateValue());
										schedule.setDay(s.getDay());
										schedule.setMonth(s.getMonth());
										schedule.setYear(s.getYear());
										schedule.setClassValue(s.getClassValue());
										schedule.setStaffCode(s.getStaffCode());
										schedule.setGroup(s.getGroup());
										schedule.setCreateDate(new Date());
										lstUpdate.add(schedule);
									}
								});
								scheduleService.importData(lstImport);
								scheduleService.updateData(lstUpdate);
								btnCancel.setVisible(false);
								btnSave.setVisible(false);
								title.setValue("");
								linkError.setLabel("");
								linkFileName.setValue("");

								int numRow = listSucces.size();
								Messagebox.show(
										Labels.getLabel("schedule.quota.upload.sucess", new String[] { "" + numRow }),
										Labels.getLabel("comfirm"), Messagebox.OK, Messagebox.INFORMATION);
								listImport.clear();
								listSucces.clear();
								listError.clear();
							}
						} catch (Exception ex) {
							logger.error(ex.getMessage(), ex);
						}
					}
				});

	}

	/**
	 *
	 * @param listData
	 * @param schedule
	 * @return
	 */
	Schedule isExist(List<Schedule> listData, Schedule schedule) {
		if (listData != null && !listData.isEmpty()) {
			for (Schedule s : listData) {
				if (schedule.getDay().equals(s.getDay()) && schedule.getThematic().equals(s.getThematic())
						&& schedule.getMonth().equals(s.getMonth()) && schedule.getStaffCode().equals(s.getStaffCode())
						&& schedule.getClassValue().equals(s.getClassValue())) {
					return s;
				}
			}
		}
		return null;
	}

	/**
	 *
	 * @param event
	 */
	public void onDownloadFile(ForwardEvent event) {
		try {
			String pathFileInput = Constants.PATH_FILE + IMPORT_PATH;

			File file = new File(pathFileInput);
			Filedownload.save(file, null);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage(), e);
		}
	}

	/**
	 *
	 */
	private void setDataDefaultInGrid() {
		gridSchedule.renderAll();
		List<Component> lstRows = gridSchedule.getRows().getChildren();
		for (int i = 0; i < lstRows.size(); i++) {
			Schedule schedule = listDataModel.get(i);
			Component row = lstRows.get(i);
			List<Component> lstCell = row.getChildren();
			setComboboxStaff(lstCell, getStaffDefault(schedule.getStaffCode(), 8), 7);

		}

	}

	/**
	 *
	 * @param staff
	 * @param type
	 * @return
	 */
	private List<Staff> getStaffDefault(String staff, int type) {
		List<Staff> staffSelected = new ArrayList<>();
		List<Staff> lstStaff = listStaff;

		if (staff != null && lstStaff != null && !lstStaff.isEmpty()) {
			for (Staff item : lstStaff) {
				if (staff.equals(item.getStaffCode())) {
					staffSelected.add(item);
					break;
				}
			}
		}
		return staffSelected;
	}

	/**
	 *
	 * @param lstCell
	 * @param selectedIndex
	 * @param columnIndex
	 */
	private void setComboboxStaff(List<Component> lstCell, List<Staff> selectedIndex, int columnIndex) {
		Combobox cbxStaff;
		Component component = lstCell.get(columnIndex).getFirstChild();
		List<Staff> lstStaff = listStaff;

		if (component != null && component instanceof Combobox) {
			cbxStaff = (Combobox) component;
			ListModelList<Staff> listDataModelParam = new ListModelList<Staff>(lstStaff);
			listDataModelParam.setSelection(selectedIndex);
			cbxStaff.setModel(listDataModelParam);
		}
	}

	/**
	 *
	 */
	public void onCheck$show() {
		lstScheduleFilter.clear();
		lstSchedule.clear();
		isShow = show.isChecked();
//        isShow = show.getValue();
		btnDeleteData.setVisible(isShow);
		reloadGrid();
	}

	/**
	 *
	 * @param event
	 */
	public void onFileError(ForwardEvent event) {
		try {

			ExcelWriter<Schedule> excelWriter = new ExcelWriter<>();

			int index = 0;
			for (Schedule item : lstScheduleFilter) {
				index++;
				item.setIndex(index);
				item.setStaffName(getStaffName(item.getStaffCode()));
			}
			String pathFileInput = session.getWebApp().getRealPath(ERROR_PATH);
			String pathFileOut = session.getWebApp().getRealPath(ERROR_PATH_TEMP) + ERROR_FILE_NAME;
			Map<String, Object> beans = new HashMap<>();
			beans.put("data", listError);
			beans.put("day", DateTimeUtils.getTime(new Date(), 1));
			beans.put("month", DateTimeUtils.getTime(new Date(), 2));
			beans.put("year", DateTimeUtils.getTime(new Date(), 3));
			excelWriter.write(beans, pathFileInput, pathFileOut);
			File file = new File(pathFileOut);
			Filedownload.save(file, null);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage(), e);
		}
	}

}
