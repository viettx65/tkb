/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dvd.ckp.excel;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.FileInputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.dvd.ckp.excel.annotation.ExcelColumn;
import com.dvd.ckp.excel.annotation.ExcelEntity;
import com.dvd.ckp.excel.exception.DataTypeNotSupportedException;
import com.dvd.ckp.excel.exception.EmptyCellException;
import com.dvd.ckp.excel.exception.InvalidCellValueException;
import com.dvd.ckp.utils.Constants;
import java.io.IOException;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

/**
 *
 * @author viettx
 */
public class ExcelReader<T> {

    private final Logger logger = Logger.getLogger(ExcelReader.class);
    private String startCol;
    private String endCol;

    public Object readCell(String filePath, int row, int cell) {
        try {
            Workbook workbook = null;
            if (filePath.endsWith(Constants.XLSX)) {
                workbook = new XSSFWorkbook(new FileInputStream(filePath));
            } else {
                workbook = new HSSFWorkbook(new FileInputStream(filePath));
            }
            Sheet sheet = workbook.getSheetAt(0);

            Object value = sheet.getRow(row).getCell(cell);

            return value;
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    public List<T> read(String filePath, Class pclass) throws InvalidCellValueException, EmptyCellException, Exception {
        String signal;
        int startRowIndex;

        List<T> ret = new ArrayList<>();
        if (pclass.isAnnotationPresent(ExcelEntity.class)) {
            Annotation ann1 = pclass.getAnnotation(ExcelEntity.class);
            ExcelEntity entityAnn = (ExcelEntity) ann1;

            signal = entityAnn.signalConstant();
            startRowIndex = ((ExcelEntity) ann1).dataStartRowIndex();

        } else {
            throw new Exception("You must annotate class with @ExcelEntity");
        }

        determineColRange(pclass);

        Workbook workbook;
        if (filePath.endsWith(Constants.XLSX)) {
            workbook = new XSSFWorkbook(new FileInputStream(filePath));
        } else {
            workbook = new HSSFWorkbook(new FileInputStream(filePath));
        }
        Sheet sheet = workbook.getSheetAt(0);

//        if (!checkFileTemplate(sheet, signal)) {
//            throw new WrongFileTemplateException(String.format("File constant value miss match. Cell A1 must contain this string value: %s", signal));
//        }
        Iterator<Row> rows = sheet.iterator();
        int rowIndex = 0;
        while (rows.hasNext()) {
            if (rowIndex < startRowIndex - 1) {
                rowIndex++;
                continue;
            }
            if (rows.next() == null) {
                break;
            }

            Row row = sheet.getRow(rowIndex);
            if (row != null) {
                if (!isRowEmpty(row)) {
                    T obj = (T) pclass.newInstance();

                    for (Method method : pclass.getDeclaredMethods()) {
                        if (method.isAnnotationPresent(ExcelColumn.class)) {
                            ExcelColumn colAnn = (ExcelColumn) method.getAnnotation(ExcelColumn.class);
                            String colName = colAnn.name();
                            boolean nullable = colAnn.nullable();
                            PropertyDescriptor pd = getPropertyDescriptor(method);

                            readCellIntoObjectProperty(obj, sheet, rowIndex, colName, pd, nullable);
                        }

                    }
                    ret.add(obj);
                }
            }

            rowIndex++;
        }
        return ret;
    }

    private boolean isRowEmpty(Row row) {
        int startColIndex = CellReference.convertColStringToIndex(startCol);
        int endColIndex = CellReference.convertColStringToIndex(endCol);
        DataFormatter dataFormatter = new DataFormatter();

        int i = startColIndex;
        while (i <= endColIndex) {
            Cell cell = row.getCell(i);
            if (cell != null) {
                String cellValue = dataFormatter.formatCellValue(cell);
                if (cellValue != null && !"".equals(cellValue.trim())) {
                    return false;
                }
            }
            i++;
        }

        return true;
    }

    /**
     * Nhan dien xem vung du lieu tu cot nao den cot nao.
     *
     * @param clazz
     */
    private void determineColRange(Class clazz) {
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(ExcelColumn.class)) {
                ExcelColumn colAnn = (ExcelColumn) method.getAnnotation(ExcelColumn.class);
                String colName = colAnn.name();
                if (startCol == null || endCol == null) {
                    if (startCol == null) {
                        startCol = colName;
                    }
                    if (endCol == null) {
                        endCol = colName;
                    }
                } else {
                    if (colName != null) {
                        if (colName.compareTo(startCol) < 0) {
                            startCol = colName;
                        }

                        if (colName.compareTo(endCol) > 0) {
                            endCol = colName;
                        }
                    }
                }
            }
        }

    }

    private boolean checkFileTemplate(XSSFSheet sheet, String signalConstant) {
        if (sheet != null && signalConstant != null) {
            XSSFRow row = sheet.getRow(0);
            if (row != null) {
                XSSFCell cellA1 = row.getCell(0);
                if (cellA1 != null) {
                    DataFormatter dataFormatter = new DataFormatter();
                    String inFileValue = dataFormatter.formatCellValue(cellA1);
                    if (inFileValue != null) {
                        if (signalConstant.equals(inFileValue.trim())) {
                            return true;
                        } else {
                            return false;
                        }
                    } else {
                        return false;
                    }

                } else {
                    return false;
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    private PropertyDescriptor getPropertyDescriptor(Method getter) {
        try {
            Class<?> clazz = getter.getDeclaringClass();
            BeanInfo info = Introspector.getBeanInfo(clazz);
            PropertyDescriptor[] props = info.getPropertyDescriptors();
            for (PropertyDescriptor pd : props) {
                if (getter.equals(pd.getReadMethod())) {
                    return pd;
                }
            }
        } catch (IntrospectionException e) {
            logger.error("Could not get properties desciptor", e);
        } catch (Exception e) {
            logger.error("Could not get properties desciptor", e);
        }
        return null;
    }

    /**
     * A long method covers all supported types, read original value from excel
     * sheet, convert it into appropriate type and finally call setter method to
     * set value into object's property
     *
     * @author ThanhNT - Nodo JSC
     *
     * @param obj
     * @param sheet
     * @param rowNum
     * @param columnName
     * @param pd
     * @throws Exception
     */
    private void readCellIntoObjectProperty(Object obj, Sheet sheet, int rowNum, String columnName,
            PropertyDescriptor pd, boolean nullable) throws Exception {
        if (sheet != null && pd != null) {
            String propertyTypeName = pd.getPropertyType().getName();
            Row row = sheet.getRow(rowNum);
            DataFormatter dataFormatter = new DataFormatter();
            Method setterMethod = pd.getWriteMethod();

            if (row != null) {
                int colIndex = CellReference.convertColStringToIndex(columnName);
                Cell cell = row.getCell(colIndex);

                // Primitive types
                String booleanTypeName = boolean.class.getName();
                String byteTypeName = byte.class.getName();
                String charTypeName = char.class.getName();
                String shortTypeName = char.class.getName();
                String intTypeName = int.class.getName();
                String longTypeName = long.class.getName();
                String floatTypeName = float.class.getName();
                String doubleTypeName = double.class.getName();
                String booleanClassTypeName = Boolean.class.getName();
                String longClassTypeName = Long.class.getName();
                String stringClassTypeName = String.class.getName();
                String dateClassTypeName = Date.class.getName();
                String bigIntClassTypeName = BigInteger.class.getName();
                String bigDecimalClassTypeName = BigDecimal.class.getName();
                String intClassTypeName = Integer.class.getName();
                String doubleClassTypeName = Double.class.getName();

                if (propertyTypeName.equals(booleanTypeName)) {
                    if (cell == null || dataFormatter.formatCellValue(cell) == null || "".equals(dataFormatter.formatCellValue(cell).trim())) {
                        if (nullable) {
                            setterMethod.invoke(obj, false);
                        } else {
                            EmptyCellException ex = new EmptyCellException(String.format("Cell %s%d is empty but value is required", columnName, rowNum + 1));
                            ex.setColumnName(columnName);
                            ex.setRow(rowNum);
                            throw ex;
                        }
                    } else {
                        String val = dataFormatter.formatCellValue(cell);
                        boolean boolVal = true;
                        if ("0".equals(val) || "f".equalsIgnoreCase(val) || "false".equalsIgnoreCase(val)) {
                            boolVal = false;
                        }
                        setterMethod.invoke(obj, boolVal);
                    }

                } else if (propertyTypeName.equals(byteTypeName)) {
                    throw new DataTypeNotSupportedException("byte type is not yet implemented");
                } else if (propertyTypeName.equals(charTypeName)) {
                    throw new DataTypeNotSupportedException("char type is not yet implemented");
                } else if (propertyTypeName.equals(shortTypeName)) {
                    throw new DataTypeNotSupportedException("short type is not yet implemented");
                } else if (propertyTypeName.equals(intTypeName)) {
                    if (cell == null || dataFormatter.formatCellValue(cell) == null || "".equals(dataFormatter.formatCellValue(cell).trim())) {
                        if (nullable) {
                            setterMethod.invoke(obj, 0);
                        } else {
                            throwEmptyCellException(rowNum, columnName);
                        }
                    } else {
                        String val = dataFormatter.formatCellValue(cell).trim();
                        try {
                            int intVal = Integer.parseInt(val);
                            setterMethod.invoke(obj, intVal);
                        } catch (NumberFormatException nex) {
                            throwInvalidCellValueException(nex, rowNum, columnName);
                        }

                    }

                } else if (propertyTypeName.equals(longTypeName)) {
                    if (cell == null || dataFormatter.formatCellValue(cell) == null || "".equals(dataFormatter.formatCellValue(cell).trim())) {
                        if (nullable) {
                            setterMethod.invoke(obj, 0L);
                        } else {
                            throwEmptyCellException(rowNum, columnName);
                        }
                    } else {
                        String val = dataFormatter.formatCellValue(cell).trim();
                        Long longObjVal = Long.valueOf(val);
                        long longVal = longObjVal;
                        setterMethod.invoke(obj, longVal);
                    }
                } else if (propertyTypeName.equals(floatTypeName)) {
                    if (cell == null || dataFormatter.formatCellValue(cell) == null || "".equals(dataFormatter.formatCellValue(cell).trim())) {
                        if (nullable) {
                            setterMethod.invoke(obj, 0L);
                        } else {
                            throwEmptyCellException(rowNum, columnName);
                        }
                    } else {
                        String val = dataFormatter.formatCellValue(cell).trim();
                        Float fVal = Float.valueOf(val);
                        setterMethod.invoke(obj, fVal);
                    }

                } else if (propertyTypeName.equals(doubleTypeName)) {
                    if (cell == null || dataFormatter.formatCellValue(cell) == null || "".equals(dataFormatter.formatCellValue(cell).trim())) {
                        if (nullable) {
                            setterMethod.invoke(obj, 0L);
                        } else {
                            throwEmptyCellException(rowNum, columnName);
                        }
                    } else {
                        String val = dataFormatter.formatCellValue(cell).trim();
                        Double dVal = Double.valueOf(val);
                        setterMethod.invoke(obj, dVal);
                    }

                } // End Primitive types
                else if (booleanClassTypeName.equals(propertyTypeName)) {
                    if (cell == null || dataFormatter.formatCellValue(cell) == null || "".equals(dataFormatter.formatCellValue(cell).trim())) {
                        if (!nullable) {
                            throwEmptyCellException(rowNum, columnName);
                        }
                    } else {
                        try {
                            String val = dataFormatter.formatCellValue(cell).trim();
                            Boolean boolObj = new Boolean(val);
                            setterMethod.invoke(obj, boolObj);
                        } catch (Exception e) {
                            throwInvalidCellValueException(e, rowNum, columnName);
                        }
                    }
                } else if (longClassTypeName.equals(propertyTypeName)) {
                    if (cell == null || dataFormatter.formatCellValue(cell) == null || "".equals(dataFormatter.formatCellValue(cell).trim())) {
                        if (!nullable) {
                            throwEmptyCellException(rowNum, columnName);
                        }
                    } else {
                        try {
                            String val = dataFormatter.formatCellValue(cell).trim();
                            setterMethod.invoke(obj, Long.valueOf(val));
                        } catch (NumberFormatException e) {
                            throwInvalidCellValueException(e, rowNum, columnName);
                        }
                    }

                } else if (stringClassTypeName.equals(propertyTypeName)) {
                    if (cell == null || dataFormatter.formatCellValue(cell) == null || "".equals(dataFormatter.formatCellValue(cell).trim())) {
                        if (!nullable) {
                            throwEmptyCellException(rowNum, columnName);
                        }
                    } else {
                        String val = dataFormatter.formatCellValue(cell).trim();
                        setterMethod.invoke(obj, val);
                    }
                } else if (dateClassTypeName.equals(propertyTypeName)) {
                    if (cell == null || dataFormatter.formatCellValue(cell) == null || "".equals(dataFormatter.formatCellValue(cell).trim())) {
                        if (!nullable) {
                            throwEmptyCellException(rowNum, columnName);
                        }
                    } else {
                        try {
                            Date date = cell.getDateCellValue();
                            setterMethod.invoke(obj, date);
                        } catch (Exception e) {
                            throwInvalidCellValueException(e, rowNum, columnName);
                        }
                    }

                } else if (bigIntClassTypeName.equals(propertyTypeName)) {
                    if (cell == null || dataFormatter.formatCellValue(cell) == null || "".equals(dataFormatter.formatCellValue(cell).trim())) {
                        if (!nullable) {
                            throwEmptyCellException(rowNum, columnName);
                        }
                    } else {
                        try {
                            String val = dataFormatter.formatCellValue(cell).trim();
                            BigInteger bigInteger = new BigInteger(val);
                            setterMethod.invoke(obj, bigInteger);
                        } catch (Exception e) {
                            throwInvalidCellValueException(e, rowNum, columnName);
                        }

                    }

                } else if (bigDecimalClassTypeName.equals(propertyTypeName)) {
                    if (cell == null || dataFormatter.formatCellValue(cell) == null || "".equals(dataFormatter.formatCellValue(cell).trim())) {
                        if (!nullable) {
                            throwEmptyCellException(rowNum, columnName);
                        }
                    } else {
                        try {
                            String val = dataFormatter.formatCellValue(cell).trim();
                            BigDecimal bigDecimal = new BigDecimal(val);
                            setterMethod.invoke(obj, bigDecimal);
                        } catch (Exception e) {
                            throwInvalidCellValueException(e, rowNum, columnName);
                        }
                    }
                } else if (intClassTypeName.equals(propertyTypeName)) {
                    if (cell == null || dataFormatter.formatCellValue(cell) == null || "".equals(dataFormatter.formatCellValue(cell).trim())) {
                        if (!nullable) {
                            throwEmptyCellException(rowNum, columnName);
                        }
                    } else {
                        try {
                            String val = dataFormatter.formatCellValue(cell).trim();
                            Integer intObj = Integer.valueOf(val);
                            setterMethod.invoke(obj, intObj);
                        } catch (Exception e) {
                            throwInvalidCellValueException(e, rowNum, columnName);
                        }
                    }
                } else if (doubleClassTypeName.equals(propertyTypeName)) {
                    if (cell == null || dataFormatter.formatCellValue(cell) == null || "".equals(dataFormatter.formatCellValue(cell).trim())) {
                        if (!nullable) {
                            throwEmptyCellException(rowNum, columnName);
                        }
                    } else {
                        try {
                            String val = dataFormatter.formatCellValue(cell).trim();
                            Double dblObj = Double.valueOf(val);
                            setterMethod.invoke(obj, dblObj);
                        } catch (Exception e) {
                            throwInvalidCellValueException(e, rowNum, columnName);
                        }
                    }
                } else {
                    throw new DataTypeNotSupportedException(String.format("%s is not supported", propertyTypeName));
                }
            } else {
                throw new Exception(String.format("Row %d is null", rowNum + 1));
            }
        }
    }

    private void throwEmptyCellException(int rowNum, String columnName) throws EmptyCellException {
        int humanRowNum = rowNum + 1;
        EmptyCellException ex = new EmptyCellException(String.format("Cell %s%d is empty but value is required", columnName, humanRowNum));
        ex.setColumnName(columnName);
        ex.setRow(humanRowNum);
        throw ex;
    }

    private void throwInvalidCellValueException(Exception originalException, int rowNum, String columnName) throws InvalidCellValueException {
        int humanRowNum = rowNum + 1;
        InvalidCellValueException ex
                = new InvalidCellValueException(String.format("Cell %s%d contains invalid value", columnName, humanRowNum), originalException);
        ex.setRow(humanRowNum);
        ex.setColumnName(columnName);

        throw ex;
    }
}
