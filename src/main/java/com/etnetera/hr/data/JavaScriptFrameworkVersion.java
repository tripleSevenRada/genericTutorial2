package com.etnetera.hr.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.Future;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDate;

@Entity
@Table(name = "details")
public class JavaScriptFrameworkVersion {

    static final int VERSION_MAJOR_DEFAULT = 0;
    static final int HYPE_LEVEL_DEFAULT = 0;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @PositiveOrZero(message = "Positive or zero")
    private int versionMajor;
    @PositiveOrZero(message = "Positive or zero")
    private int hypeLevel;
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    @Future(message = "We are only interested in cutting edge stuff")
    private LocalDate deprecationDate;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "framework_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    private JavaScriptFramework framework;

    public JavaScriptFrameworkVersion(){}

    public JavaScriptFrameworkVersion(
            int versionMajor,
            int hypeLevel,
            LocalDate deprecationDate) {
        this.versionMajor = versionMajor;
        this.deprecationDate = deprecationDate;
        this.hypeLevel = hypeLevel;
    }

    public JavaScriptFrameworkVersion(
            int versionMajor,
            int hypeLevel,
            int deprecationDateYear, // TODO validation here, no constant used
            @Valid
            @Min(1)
            @Max(12)
            int deprecationDateMonth
            ) {
        this.versionMajor = versionMajor;
        this.hypeLevel = hypeLevel;
        this.deprecationDate = LocalDate.of(deprecationDateYear, deprecationDateMonth, 1);
    }

    public int getVersionMajor() {
        return versionMajor;
    }

    public void setVersionMajor(int versionMajor) {
        this.versionMajor = versionMajor;
    }

    public int getHypeLevel() {
        return hypeLevel;
    }

    public void setHypeLevel(int hypeLevel) {
        this.hypeLevel = hypeLevel;
    }

    public LocalDate getDeprecationDate() {
        return deprecationDate;
    }

    public void setDeprecationDate(LocalDate deprecationDate) {
        this.deprecationDate = deprecationDate;
    }

    public JavaScriptFramework getFramework() {
        return framework;
    }

    public void setFramework(JavaScriptFramework framework) {
        this.framework = framework;
    }
}
