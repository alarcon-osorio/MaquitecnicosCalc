package com.server.calc.exporter;

import com.server.calc.entity.DataRegistry;
import lombok.extern.log4j.Log4j2;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import javax.servlet.http.HttpServletResponse;

import javax.servlet.ServletOutputStream;
import java.io.IOException;
import java.util.List;

@Log4j2
public class ExcelExportUnlisted {
    private XSSFWorkbook workbook;
    private XSSFSheet sheet;
    private List<DataRegistry> dataRegistries;

    public ExcelExportUnlisted(List<DataRegistry> dataRegistries) {
        this.dataRegistries = dataRegistries;
        workbook = new XSSFWorkbook();
    }

    private void writeHeaderLine() {
        sheet = workbook.createSheet("DataRegistry");

        Row row = sheet.createRow(0);

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(16);
        style.setFont(font);

        createCell(row, 0, "id");
        createCell(row, 1, "Fecha de registro");
        createCell(row, 2, "Referncia OEM");
        createCell(row, 3, "Referencia SAMPA o DT ");
        createCell(row, 4, "Cantidad");
        createCell(row, 5, "Motivo");
        createCell(row, 6, "Venta - (COP)");
        createCell(row, 7, "Cantidad sugerida");
        createCell(row, 8, "Marca sugerida");
        createCell(row, 9, "Usuario");
        createCell(row, 10, "Cliente");
        createCell(row, 11, "Observaciones");

    }

    private void createCell(Row row, int columnCount, Object value) {
        sheet.autoSizeColumn(columnCount);
        Cell cell = row.createCell(columnCount);
        if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        }else if (value instanceof Long) {
            cell.setCellValue((Long) value);
        }else{
            cell.setCellValue((String) value);
        }
    }

    private void writeDataLines() {
        int rowCount = 1;

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(10);
        style.setFont(font);

        for (DataRegistry data : dataRegistries) {
            Row row = sheet.createRow(rowCount++);
            int columnCount = 0;

            String[] splitData = data.getDatereg().split("-");

            String year = splitData[0];
            String mounth = splitData[1];
            String day = splitData[2];


            createCell(row, columnCount++, data.getId());
            createCell(row, columnCount++, day + "/" + mounth + "/" + year);
            createCell(row, columnCount++, data.getRefoem());
            createCell(row, columnCount++, data.getRefsadt());
            createCell(row, columnCount++, data.getCant());
            createCell(row, columnCount++, data.getReason());
            createCell(row, columnCount++, data.getCop());
            createCell(row, columnCount++, data.getCantsug());
            createCell(row, columnCount++, data.getBrand());
            createCell(row, columnCount++, data.getUser());
            createCell(row, columnCount++, data.getClient());
            createCell(row, columnCount++, data.getObs());

        }
    }

    public void export(HttpServletResponse response) throws IOException {
        writeHeaderLine();
        writeDataLines();

        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();

        outputStream.close();

    }


}
