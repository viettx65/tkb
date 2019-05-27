/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dvd.ckp.controller;

import com.dvd.ckp.business.service.ParamService;
import com.dvd.ckp.component.MyListModel;
import com.dvd.ckp.domain.Param;
import com.dvd.ckp.utils.Constants;
import com.dvd.ckp.utils.SpringConstant;
import com.dvd.ckp.utils.StringUtils;
import com.dvd.ckp.utils.StyleUtils;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Grid;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;

/**
 *
 * @author daond
 */
public class ParamController extends GenericForwardComposer {

    private static final Logger logger = Logger.getLogger(ParamController.class);
    @WireVariable
    protected ParamService paramService;
    @Wire
    private Grid lstParam;

    @Wire
    private Textbox txtFilterParamName;
    ListModelList<Param> listDataModel;
    private List<Param> lstParams;
    @Wire
    private Combobox cbxParamFilter;
    MyListModel<Param> listModelParamKey;

    private List<Param> lstKeyParams;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        paramService = (ParamService) SpringUtil.getBean(SpringConstant.PARAM_SERVICES);
        lstParams = new ArrayList<>();
        List<Param> vlstRole = paramService.getAllParam();
        if (vlstRole != null) {
            lstParams.addAll(getParamKeyName(vlstRole));
        }
        listDataModel = new ListModelList(lstParams);
        lstParam.setModel(listDataModel);

        getDistinctParamKeyName();

        listModelParamKey = new MyListModel(lstKeyParams);
        cbxParamFilter.setModel(listModelParamKey);
        setDataDefaultInGrid();
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
     * Save
     *
     * @param event
     */
    public void onSave(ForwardEvent event) {
        Row rowSelected = (Row) event.getOrigin().getTarget().getParent().getParent();
        List<Component> lstCell = rowSelected.getChildren();
        Param c = rowSelected.getValue();
        Param paramSave = getDataInRow(lstCell);
        paramSave.setParamId(c.getParamId());
        paramSave.setStatus(Constants.STATUS_ACTIVE);
        paramService.insertOrUpdateParam(paramSave);
        StyleUtils.setDisableComponent(lstCell, 4);
        reloadGrid();
    }

    /**
     * Add row
     */
    public void onClick$add() {
        Param paramAdd = new Param();
        listDataModel.add(0, paramAdd);
        lstParam.setActivePage(Constants.FIRST_INDEX);
        lstParam.setModel(listDataModel);
        lstParam.renderAll();
        List<Component> lstCell = lstParam.getRows().getChildren().get(0).getChildren();
        StyleUtils.setEnableComponent(lstCell, 4);
        setDataDefaultInGrid();
    }

    /**
     * Get object customer
     *
     * @param lstCell
     * @return
     */
    private Param getDataInRow(List<Component> lstCell) {
        Param paramData = new Param();
        Combobox txtParamKey = (Combobox) lstCell.get(1).getFirstChild();
        Textbox txtParamName = (Textbox) lstCell.get(2).getFirstChild();
        Longbox longValue = (Longbox) lstCell.get(3).getFirstChild();
        paramData.setParamKey(txtParamKey.getSelectedItem().getValue());
        paramData.setParamName(txtParamName.getValue());
        paramData.setParamValue(longValue.getValue());
        return paramData;
    }

    /**
     * Reload grid
     */
    private void reloadGrid() {
        lstParams = paramService.getAllParam();
        listDataModel = new ListModelList(getParamKeyName(lstParams));
        lstParam.setModel(listDataModel);
        listModelParamKey = new MyListModel(lstKeyParams);
        cbxParamFilter.setModel(listModelParamKey);
        setDataDefaultInGrid();
    }

    public void onSelect$cbxParamFilter() {
        Param paramSearch = new Param();
        String vstrKeyParam = null;
        if (cbxParamFilter.getSelectedItem() != null) {
            vstrKeyParam = cbxParamFilter.getSelectedItem().getValue();
        }
        paramSearch.setParamKey(vstrKeyParam);
        String vstrRoleName = txtFilterParamName.getValue();
        paramSearch.setParamName(vstrRoleName);
        filter(paramSearch);
    }

    public void onChange$txtFilterRoleName() {
        Param paramSearch = new Param();
        String vstrKeyParam = null;
        if (cbxParamFilter.getSelectedItem() != null) {
            vstrKeyParam = cbxParamFilter.getSelectedItem().getValue();
        }
        paramSearch.setParamKey(vstrKeyParam);
        String vstrRoleName = txtFilterParamName.getValue();
        paramSearch.setParamName(vstrRoleName);
        filter(paramSearch);
    }

    private void filter(Param role) {
        List<Param> vlstParam = new ArrayList<>();
        if (lstParams != null && !lstParams.isEmpty() && role != null) {
            if (!StringUtils.isValidString(role.getParamKey())
                    && !StringUtils.isValidString(role.getParamName())) {
                vlstParam.addAll(lstParams);
            } else {
                for (Param c : lstParams) {
                    //tim theo ma va ten`
                    if (StringUtils.isValidString(role.getParamKey()) && StringUtils.isValidString(role.getParamName())) {
                        if ((StringUtils.isValidString(c.getParamKey()) && c.getParamKey().toLowerCase().contains(role.getParamKey().toLowerCase()))
                                && (StringUtils.isValidString(c.getParamName()) && c.getParamName().toLowerCase().contains(role.getParamName().toLowerCase()))) {
                            vlstParam.add(c);
                        }
                    } //tim theo role code
                    else if (StringUtils.isValidString(role.getParamKey()) && !StringUtils.isValidString(role.getParamName())) {
                        if (StringUtils.isValidString(c.getParamKey()) && c.getParamKey().toLowerCase().contains(role.getParamKey().toLowerCase())) {
                            vlstParam.add(c);
                        }
                    } //tim theo role name
                    else if (!StringUtils.isValidString(role.getParamKey()) && StringUtils.isValidString(role.getParamName())) {
                        if (StringUtils.isValidString(c.getParamName()) && c.getParamName().toLowerCase().contains(role.getParamName().toLowerCase())) {
                            vlstParam.add(c);
                        }
                    }
                }
            }
        }
        listDataModel = new ListModelList(vlstParam);
        lstParam.setModel(listDataModel);
        setDataDefaultInGrid();
    }

    /**
     * Delete
     *
     * @param event
     */
    public void onDelete(ForwardEvent event) {
        Messagebox.show("Bạn có chắc chắn muốn xóa không?", "Xác nhận", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION, new EventListener() {
            public void onEvent(Event e) {
                if (Messagebox.ON_YES.equals(e.getName())) {
                    Row rowSelected = (Row) event.getOrigin().getTarget().getParent().getParent();
                    Param c = rowSelected.getValue();
                    c.setStatus(Constants.STATUS_INACTIVE);
                    paramService.insertOrUpdateParam(c);
                    reloadGrid();
                }
            }
        });
    }

    private List<Param> getParamKeyName(List<Param> plstParam) {
        List<Param> result = new ArrayList<>();
        if (plstParam != null && !plstParam.isEmpty()) {
            for (Param p : plstParam) {
                p.setParamKeyName(Constants.getParamFromType(p.getParamKey()));
                result.add(p);
            }
        }
        return result;
    }

    private void getDistinctParamKeyName() {
        if (lstKeyParams == null || lstKeyParams.isEmpty()) {
//            lstKeyParams = paramService.getDistinctParamKey();
            lstKeyParams = Constants.getParamKey();
            if (lstKeyParams != null && !lstKeyParams.isEmpty()) {
                for (Param p : lstKeyParams) {
                    p.setParamKeyName(Constants.getParamFromType(p.getParamKey()));
                }
            }
        }
    }

    private void setDataDefaultInGrid() {
        lstParam.renderAll();
        List<Component> lstRows = lstParam.getRows().getChildren();
        if (lstRows != null && !lstRows.isEmpty()) {
            for (int i = 0; i < lstRows.size(); i++) {
                Param param = listDataModel.get(i);
                Component row = lstRows.get(i);
                List<Component> lstCell = row.getChildren();
                setDataParam(lstCell, getParamDefault(param.getParamKey()), 1);
            }
        }
    }

    private List<Param> getParamDefault(String paramKey) {
        List<Param> paramSelected = new ArrayList<>();
        if (paramKey != null && lstKeyParams != null && !lstKeyParams.isEmpty()) {
            for (Param customer : lstKeyParams) {
                if (paramKey.equalsIgnoreCase(customer.getParamKey())) {
                    paramSelected.add(customer);
                    break;
                }
            }
        }
        return paramSelected;
    }

    private void setDataParam(List<Component> lstCell, List<Param> selectedIndex, int columnIndex) {
        Combobox combobox = null;
        Component component = lstCell.get(columnIndex).getFirstChild();
        if (component != null && component instanceof Combobox) {
            combobox = (Combobox) component;
            MyListModel listDataModelParam = new MyListModel(lstKeyParams);
            listDataModelParam.setSelection(selectedIndex);
            combobox.setModel(listDataModelParam);
        }

    }

}
