package com.aji_prayitno.excel.exporter;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.util.List;

import org.apache.poi.ss.usermodel.BorderStyle;

import com.aji_prayitno.excel.exporter.style.TableStyle;

public class Main {

	public static void main(String[] args) {
		ExcelService excelService = new ExcelService();
		byte[] bytes = excelService.builder()
			.addSheet("Data 1", sheetStep -> {
				sheetStep
				.title("Data table 1")
				.title("Data table 1.1")
				.addTable("table_name1", Dto.class, tableStep -> {
					tableStep
					.addColumn("No.", t -> t.getNo())
					.addColumn("Name", t -> t.getName(), config -> config.bold().autoSize())
					.addColumn("Gender", t -> t.getGender(), config -> config.autoSize())
					.addColumn("DOB", t -> t.getDob(), config -> config.styleCustom("dd/mm/yyyy"))
					.addColumn("Address", t -> t.getAddress(), config -> config.autoSize())
					.addColumn("City", t -> t.getCity())
					.addColumn("Province", t -> t.getProvince())
					.addData(datas());
				});
			})
			.addSheet("Data 2", sheetStep -> {
				sheetStep
				.title("Data table 2")
				.addTable(Dto.class, tableStep -> {
					tableStep
					.tableStyle(TableStyle.OUTSIDE)
					.borderStyle(BorderStyle.DASH_DOT_DOT)
					.addColumn("No.", t -> t.getNo())
					.addColumn(column -> column.add("Name"), t -> t.getName())
					.addColumn(column -> column.add("Gender"), t -> t.getGender(), config -> config.bold().autoSize())
					.addColumn(column -> column.add("DOB"), t -> t.getDob(), config -> config.styleCustom("dd/mm/yyyy"))
					.addColumn(column -> column.add("Address").add("Address"), t -> t.getAddress(), config -> config.autoSize())
					.addColumn(column -> column.add("Address", "City"), t -> t.getCity())
					.addColumn(column -> column.add("Address", "Province"), t -> t.getProvince(), config -> config.shrinkToFit())
					.addData(datas());
				});
			})
			.addSheet("Data 3", sheetStep -> {
				sheetStep
				.title("Data table 3")
				.title("Data table 3.1")
				.addTable(Dto.class, tableStep -> {
					tableStep
					.tableStyle(TableStyle.GRID)
					.addColumn("No.", t -> t.getNo())
					.addColumn(column -> column.add("Name"), t -> t.getName(), config -> config.autoSize())
					.addColumn(column -> column.add("Gender"), t -> t.getGender(), config -> config.bold().autoSize())
					.addColumn(column -> column.add("DOB"), t -> t.getDob(), config -> config.styleCustom("dd/mm/yyyy"))
					.addColumn(column -> column.add("Address").add("Address"), t -> t.getAddress(), config -> config.autoSize())
					.addColumn(column -> column.add("Address", "City"), t -> t.getCity())
					.addColumn(column -> column.add("Address", "Province"), t -> t.getProvince(), config -> config.right())
					.addData(datas());
				});
			})
			.build();

		try {
			Path filePath = Paths.get(System.getProperty("user.dir"))
					.resolve("target")
					.resolve("test.xlsx");

			Files.write(filePath, bytes, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static List<Dto> datas(){
		return List.of(
			new Dto(1, "Nama Panjang", "L", LocalDate.parse("1990-01-01"), "Alamat Panjang", "Kota Panjang Banget", "Provinsi"),
			new Dto(2, "Nama Panjang Banget", "L", LocalDate.parse("1990-01-01"),"Alamat Panjang Banget", "Kota Panjang Banget", "Provinsi Panjang"),
			new Dto(3, "Nama", "L", LocalDate.parse("1990-01-01"),"Alamat", "Kota", "Provinsi Panjang")
		);
	}
	
	public static class Dto{
		private Integer no;
		private String name;
		private String gender;
		private LocalDate dob;
		private String address;
		private String city;
		private String province;
		
		public Dto(
			Integer no, String name, String gender, LocalDate dob,
			String address, String city, String province
		) {
			this.no = no;
			this.name = name;
			this.gender = gender;
			this.dob = dob;
			this.address = address;
			this.city = city;
			this.province = province;
		}
		public Integer getNo() {
			return no;
		}
		public void setNo(Integer no) {
			this.no = no;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getGender() {
			return gender;
		}
		public void setGender(String gender) {
			this.gender = gender;
		}
		public LocalDate getDob() {
			return dob;
		}
		public void setDob(LocalDate dob) {
			this.dob = dob;
		}
		public String getAddress() {
			return address;
		}
		public void setAddress(String address) {
			this.address = address;
		}
		public String getCity() {
			return city;
		}
		public void setCity(String city) {
			this.city = city;
		}
		public String getProvince() {
			return province;
		}
		public void setProvince(String province) {
			this.province = province;
		}
	}
}
