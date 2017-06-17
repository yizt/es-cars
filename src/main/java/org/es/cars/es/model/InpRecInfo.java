package org.es.cars.es.model;

/**
 * Created by mick.yi on 2017/4/28.
 * 住院记录对象
 */
public class InpRecInfo {
    private String health_event_id;
    private String inp_no;
    private String org_id;
    private String org_name;
    private String patient_id;
    private String patient_name;
    private String sex_name;
    private String age_year;
    private String inp_date;
    private String dishospital_date;
    private String inp_dept_id;
    private String inp_dept_name;
    private String area_no;
    private String area_name;
    private String inp_room;
    private String bed_no;


    public String getInp_no() {
        return inp_no;
    }

    public void setInp_no(String inp_no) {
        this.inp_no = inp_no;
    }

    public String getOrg_id() {
        return org_id;
    }

    public void setOrg_id(String org_id) {
        this.org_id = org_id;
    }

    public String getOrg_name() {
        return org_name;
    }

    public void setOrg_name(String org_name) {
        this.org_name = org_name;
    }

    public String getPatient_id() {
        return patient_id;
    }

    public void setPatient_id(String patient_id) {
        this.patient_id = patient_id;
    }

    public String getPatient_name() {
        return patient_name;
    }

    public void setPatient_name(String patient_name) {
        this.patient_name = patient_name;
    }

    public String getSex_name() {
        return sex_name;
    }

    public void setSex_name(String sex_name) {
        this.sex_name = sex_name;
    }

    public String getAge_year() {
        return age_year;
    }

    public void setAge_year(String age_year) {
        this.age_year = age_year;
    }

    public String getInp_date() {
        return inp_date;
    }

    public void setInp_date(String inp_date) {
        this.inp_date = inp_date;
    }

    public String getDishospital_date() {
        return dishospital_date;
    }

    public void setDishospital_date(String dishospital_date) {
        this.dishospital_date = dishospital_date;
    }

    public String getInp_dept_id() {
        return inp_dept_id;
    }

    public void setInp_dept_id(String inp_dept_id) {
        this.inp_dept_id = inp_dept_id;
    }

    public String getInp_dept_name() {
        return inp_dept_name;
    }

    public void setInp_dept_name(String inp_dept_name) {
        this.inp_dept_name = inp_dept_name;
    }

    public String getArea_name() {
        return area_name;
    }

    public void setArea_name(String area_name) {
        this.area_name = area_name;
    }

    public String getHealth_event_id() {
        return health_event_id;
    }

    public void setHealth_event_id(String health_event_id) {
        this.health_event_id = health_event_id;
    }

    public String getArea_no() {
        return area_no;
    }

    public void setArea_no(String area_no) {
        this.area_no = area_no;
    }

    public String getInp_room() {
        return inp_room;
    }

    public void setInp_room(String inp_room) {
        this.inp_room = inp_room;
    }

    public String getBed_no() {
        return bed_no;
    }

    public void setBed_no(String bed_no) {
        this.bed_no = bed_no;
    }

    /**
     * 返回报表接口需要的格式
     * @return
     */
    public String[] toStringArray(){
        return new String[]{
                this.inp_no,
                this.patient_name,
                this.sex_name,
                this.age_year,
                this.inp_dept_name,
                this.area_name,
                this.bed_no,
                this.dishospital_date ==null ? "在院":"出院",
                this.inp_date
        };
    }
}
