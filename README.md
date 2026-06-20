# Excel Exporter

Library Java berbasis Apache POI untuk export dan import Excel melalui API fluent `ExcelService`.

## Install dari GitHub

Library ini bisa digunakan dari GitHub melalui JitPack setelah repository dipush dan memiliki tag release.

Repository ini:

```text
https://github.com/ajiprays/excel-poi-wrapper
```

### Maven

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>com.github.ajiprays</groupId>
        <artifactId>excel-poi-wrapper</artifactId>
        <version>v1.0.0</version>
    </dependency>
</dependencies>
```

### Gradle

```gradle
repositories {
    mavenCentral()
    maven { url "https://jitpack.io" }
}

dependencies {
    implementation "com.github.ajiprays:excel-poi-wrapper:v1.0.0"
}
```

### Untuk Maintainer

```bash
git tag v1.0.0
git push origin v1.0.0
```

Setelah tag dipush, cek build di:

```text
https://jitpack.io/#ajiprays/excel-poi-wrapper/v1.0.0
```

## Membuat Service

```java
import com.aji_prayitno.excel.ExcelService;

ExcelService excelService = new ExcelService();
```

## Contoh DTO

DTO import wajib memiliki constructor kosong karena importer membuat instance DTO lewat reflection.

```java
import java.time.LocalDate;

public class PersonDto {
    private Integer no;
    private String name;
    private LocalDate dob;
    private Long height;
    private Boolean active;

    public PersonDto() {
    }

    public PersonDto(Integer no, String name, LocalDate dob, Long height, Boolean active) {
        this.no = no;
        this.name = name;
        this.dob = dob;
        this.height = height;
        this.active = active;
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

    public LocalDate getDob() {
        return dob;
    }

    public void setDob(LocalDate dob) {
        this.dob = dob;
    }

    public Long getHeight() {
        return height;
    }

    public void setHeight(Long height) {
        this.height = height;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
```

## Export ke XLSX

Contoh ini membuat workbook `.xlsx` dengan satu sheet dan satu Excel Table bernama `person_table`.

```java
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import com.aji_prayitno.excel.ExcelService;

public class ExportExample {
    public static void main(String[] args) throws Exception {
        ExcelService excelService = new ExcelService();

        List<PersonDto> people = List.of(
            new PersonDto(1, "Aji", LocalDate.parse("1990-01-01"), 170L, true),
            new PersonDto(2, "Budi", LocalDate.parse("1991-02-10"), 165L, false)
        );

        byte[] workbookBytes = excelService.exportBuilder()
            .addSheet("People", sheet -> {
                sheet.title("People Data");
                sheet.addTable("person_table", PersonDto.class, table -> {
                    table
                        .addColumn("No", PersonDto::getNo)
                        .addColumn("Name", PersonDto::getName, config -> config.bold().autoSize())
                        .addColumn("DOB", PersonDto::getDob, config -> config.styleCustom("dd/mm/yyyy"))
                        .addColumn("Height", PersonDto::getHeight)
                        .addColumn("Active", PersonDto::getActive)
                        .addData(people);
                });
            })
            .build();

        Files.write(Path.of("people.xlsx"), workbookBytes);
    }
}
```

Jika ingin langsung menulis ke `OutputStream`:

```java
try (var output = Files.newOutputStream(Path.of("people.xlsx"))) {
    excelService.exportBuilder()
        .addSheet("People", sheet -> {
            sheet.addTable("person_table", PersonDto.class, table -> {
                table
                    .addColumn("No", PersonDto::getNo)
                    .addColumn("Name", PersonDto::getName)
                    .addData(people);
            });
        })
        .build(output);
}
```

## Export Manual Table

Gunakan manual table jika ingin header bertingkat atau styling table manual.

```java
import org.apache.poi.ss.usermodel.BorderStyle;

import com.aji_prayitno.excel.exporter.style.TableStyle;

byte[] workbookBytes = excelService.exportBuilder()
    .addSheet("People", sheet -> {
        sheet.title("People Data");
        sheet.addTable(PersonDto.class, table -> {
            table
                .tableStyle(TableStyle.GRID)
                .borderStyle(BorderStyle.THIN)
                .addColumn("No", PersonDto::getNo)
                .addColumn(column -> column.add("Identity", "Name"), PersonDto::getName)
                .addColumn(column -> column.add("Identity", "DOB"), PersonDto::getDob,
                    config -> config.styleCustom("dd/mm/yyyy"))
                .addColumn("Height", PersonDto::getHeight)
                .addData(people);
        });
    })
    .build();
```

## Import XLSX dari Excel Table

Import `.xlsx` table membutuhkan file yang benar-benar memiliki Excel Table metadata. Di Excel, range harus dibuat sebagai table, bukan hanya cell biasa yang diberi border.

```java
import java.io.FileInputStream;
import java.util.List;

import com.aji_prayitno.excel.ExcelService;
import com.aji_prayitno.excel.importer.model.ImportResult;

public class ImportXlsxExample {
    public static void main(String[] args) throws Exception {
        ExcelService excelService = new ExcelService();

        try (var input = new FileInputStream("people.xlsx")) {
            List<ImportResult<PersonDto>> results = excelService.importBuilder()
                .from(input)
                .fromSheet("People")
                .fromTable("person_table", PersonDto.class, columns -> {
                    columns
                        .fromColumn("No", PersonDto::setNo)
                        .fromColumn("Name", PersonDto::setName)
                        .fromColumn("DOB", PersonDto::setDob)
                        .fromColumn("Height", PersonDto::setHeight)
                        .fromColumn("Active", PersonDto::setActive)
                        .fromColumnIgnoreNotFound("Optional Column", (target, value) -> {
                            // Column ini dilewati jika header tidak ada.
                        });
                })
                .importData();

            for (ImportResult<PersonDto> result : results) {
                PersonDto data = result.data();
                boolean hasError = !result.error().isEmpty();
            }
        }
    }
}
```

Catatan:

- `fromColumn(...)` adalah required column. Import akan gagal jika header tidak ditemukan.
- `fromColumnIgnoreNotFound(...)` adalah optional column. Import tetap berjalan jika header tidak ditemukan.
- `ImportResult.error()` berisi error konversi atau setter per row.

## Import XLSX sebagai Stream

Gunakan `importDataAsStream()` untuk file besar. Stream harus ditutup.

```java
try (var input = new FileInputStream("people.xlsx");
     var stream = excelService.importBuilder()
         .from(input)
         .fromSheet("People")
         .fromTable("person_table", PersonDto.class, columns -> {
             columns
                 .fromColumn("No", PersonDto::setNo)
                 .fromColumn("Name", PersonDto::setName);
         })
         .importDataAsStream()) {

    stream.forEach(result -> {
        PersonDto data = result.data();
    });
}
```

## Import XLS untuk File Kecil

Untuk legacy `.xls`, gunakan `fromRaw(...)`. Parameter pertama adalah index row header berbasis nol.

```java
try (var input = new FileInputStream("people.xls")) {
    List<ImportResult<PersonDto>> results = excelService.importBuilder()
        .from(input)
        .fromSheet("People")
        .fromRaw(0, PersonDto.class, columns -> {
            columns
                .fromColumn("No", PersonDto::setNo)
                .fromColumn("Name", PersonDto::setName)
                .fromColumn("DOB", PersonDto::setDob)
                .fromColumn("Height", PersonDto::setHeight)
                .fromColumn("Active", PersonDto::setActive);
        })
        .importDataSmallFile();
}
```

## Import XLS sebagai Stream

```java
try (var input = new FileInputStream("people.xls");
     var stream = excelService.importBuilder()
         .from(input)
         .fromSheet("People")
         .fromRaw(0, PersonDto.class, columns -> {
             columns
                 .fromColumn("No", PersonDto::setNo)
                 .fromColumn("Name", PersonDto::setName);
         })
         .importDataAsStream()) {

    stream.forEach(result -> {
        PersonDto data = result.data();
    });
}
```

## Pilihan Import

- `importData()` membaca semua data melalui mekanisme streaming lalu mengumpulkannya menjadi `List`.
- `importDataAsStream()` mengembalikan `Stream<ImportResult<T>>` untuk file besar.
- `importDataSmallFile()` menggunakan high-level POI API dan cocok untuk file kecil.

## Tipe Data

Importer melakukan konversi otomatis berdasarkan tipe parameter setter, antara lain:

- `String`
- primitive dan wrapper number seperti `Integer`, `Long`, `Double`
- `Boolean`
- `BigDecimal`, `BigInteger`
- `LocalDate`, `LocalDateTime`, `LocalTime`
- `Date`, `Instant`, `ZonedDateTime`
- `UUID`
- `enum`
