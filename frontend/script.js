'use strict';

let ViewModel = function () {
    let self = this;
    self.students = ko.observableArray();
    self.courses = ko.observableArray();
    self.grades = ko.observableArray();
    self.courses_select = ko.observableArray();

    self.student = {
        firstName: ko.observable(),
        lastName: ko.observable(),
        birthday: ko.observable()
    };
    self.course = {
        name: ko.observable(),
        lecturer: ko.observable()
    };
    self.gradeMetadata = {
        studentId: ko.observable(),
        course: ko.observable(),
        date: ko.observable(),
        value: ko.observable(),
        student: ko.observable(),
        studentName: ko.observable(),
        studentlink: ko.observable()
    };
    self.grade = {
        course: ko.observable(),
        date: ko.observable(),
        value: ko.observable()
    };

    self.findStudent = {
        index: ko.observable(),
        firstName: ko.observable(),
        lastName: ko.observable(),
        birthday: ko.observable(),
        birthdayCompare: ko.observable()
    };
    self.findCourse = {
        id: ko.observable(),
        name: ko.observable(),
        lecturer: ko.observable()
    };
    self.findGrade = {
        id: ko.observable(),
        value: ko.observable(),
        valueCompare: ko.observable(),
        course: ko.observable(),
        date: ko.observable(),
        dateCompare: ko.observable()
    };

    self.compareValue = ko.observableArray([
        {name: 'Equal', value: '0'},
        {name: 'Less', value: '-1'},
        {name: 'Greater', value: '1'}
    ]);

    self.getStudents = function (url) {
        self.students.removeAll();
        $.ajax({
            url: url,
            type: 'GET',
            dataType: "json",
        }).done(function (response) {
            response.forEach(function (object) {
                var view = ko.mapping.fromJS(object, self);
                ko.computed(function () {
                    return ko.mapping.toJSON(view);
                }).subscribe(function (res) {
                    let resource = ko.mapping.fromJSON(res);
                    $.ajax({
                        url: "http://localhost:8000" + resource.link()[0].href(),
                        type: 'PUT',
                        dataType: "json",
                        contentType: "application/json",
                        data: ko.mapping.toJSON(view)
                    });
                });
                self.students.push(view);
            });
        });
    };

    self.getCourses = function (url) {
        self.courses.removeAll();
        $.ajax({
            url: url,
            type: 'GET',
            dataType: "json",
        }).done(function (response) {
            response.forEach(function (object) {
                var view = ko.mapping.fromJS(object, self);
                ko.computed(function () {
                    return ko.mapping.toJSON(view);
                }).subscribe(function (res) {
                    let resource = ko.mapping.fromJSON(res);
                    $.ajax({
                        url: "http://localhost:8000" + resource.link()[0].href(),
                        type: 'PUT',
                        dataType: "json",
                        contentType: "application/json",
                        data: ko.mapping.toJSON(view)
                    });
                });
                self.courses.push(view);
            });
        });
    };

    self.getCoursesSelect = function (url) {
        self.courses_select.removeAll();
        $.ajax({
            url: url,
            type: 'GET',
            dataType: "json",
        }).done(function (response) {
            response.forEach(function (object) {
                var view = ko.mapping.fromJS(object, self);
                ko.computed(function () {
                    return ko.mapping.toJSON(view);
                }).subscribe(function (res) {
                    let resource = ko.mapping.fromJSON(res);
                    $.ajax({
                        url: "http://localhost:8000" + resource.link()[0].href(),
                        type: 'PUT',
                        dataType: "json",
                        contentType: "application/json",
                        data: ko.mapping.toJSON(view)
                    });
                });
                self.courses_select.push(view);
            });
        });
    };

    self.getGrades = function (student) {
        self.grades.removeAll();
        self.gradeMetadata.student(student);
        self.gradeMetadata.studentlink = student.link()[0].href();
        self.gradeMetadata.studentId(student.index());
        self.gradeMetadata.studentName(student.firstName() + " " + student.lastName());
        self.grades.removeAll();
        $.ajax({
            url: "http://localhost:8000" + student.link()[0].href() + '/grades',
            type: 'GET',
            dataType: "json",
        }).done(function (result) {
            result.forEach(function (object) {
                var view = ko.mapping.fromJS(object, self);
                ko.computed(function () {
                    return ko.mapping.toJSON(view);
                }).subscribe(function (res) {
                    let resource = ko.mapping.fromJSON(res);
                    $.ajax({
                        url: "http://localhost:8000" + resource.link()[0].href(),
                        type: 'PUT',
                        dataType: "json",
                        contentType: "application/json",
                        data: ko.mapping.toJSON(view)
                    });
                });
                self.grades.push(view);
            });
        });
    };

    self.reloadGrades = function () {
        self.grades.removeAll();
        $.ajax({
            url: "http://localhost:8000" + self.gradeMetadata.studentlink + '/grades',
            type: 'GET',
            dataType: "json",
        }).done(function (result) {
            result.forEach(function (object) {
                var view = ko.mapping.fromJS(object, self);
                ko.computed(function () {
                    return ko.mapping.toJSON(view);
                }).subscribe(function (res) {
                    let resource = ko.mapping.fromJSON(res);
                    $.ajax({
                        url: "http://localhost:8000" + resource.link()[0].href(),
                        type: 'PUT',
                        dataType: "json",
                        contentType: "application/json",
                        data: ko.mapping.toJSON(view)
                    });
                });
                self.grades.push(view);
            });
        });
    };

    self.removeStudent = function (student) {
        $.ajax({
            url: "http://localhost:8000" + student.link()[0].href(),
            type: 'DELETE',
            contentType: "application/json"
        }).done(function () {
            window.location.reload(true);
        });
    };

    self.removeCourse = function (course) {
        $.ajax({
            url: "http://localhost:8000" + course.link()[0].href(),
            type: 'DELETE',
            contentType: "application/json"
        }).done(function () {
            window.location.reload(true);
        });
    };

    self.removeGrade = function (grade) {
        $.ajax({
            url: "http://localhost:8000" + grade.link()[0].href(),
            type: 'DELETE',
            contentType: "application/json"
        }).done(function () {
            window.location.reload(true);
        });
    };

    self.postStudent = function () {
        $.ajax({
            url: 'http://localhost:8000/students',
            type: 'POST',
            contentType: "application/json",
            data: ko.mapping.toJSON(self.student)
        }).done(function () {
            window.location.reload(true);
        });
    };

    self.postCourse = function () {
        $.ajax({
            url: 'http://localhost:8000/courses',
            type: 'POST',
            contentType: "application/json",
            data: ko.mapping.toJSON(self.course)
        }).done(function () {
            window.location.reload(true);
        });
    };

    self.postGrade = function () {
        self.grade.course = self.gradeMetadata.course;
        self.grade.value = self.gradeMetadata.value;
        self.grade.date = self.gradeMetadata.date;
        $.ajax({
            url: 'http://localhost:8000' + self.gradeMetadata.student().link()[0].href() + '/grades',
            type: 'POST',
            contentType: "application/json",
            data: ko.mapping.toJSON(self.grade)
        }).done(function () {
            self.gradeMetadata.course("");
            self.gradeMetadata.value("");
            self.gradeMetadata.date("");
            self.reloadGrades();
        });
    };

    Object.keys(self.findStudent).forEach(function (key) {
        self.findStudent[key].subscribe(function (val) {
            var params = ko.mapping.toJS(self.findStudent);
            Object.keys(params).forEach(key => params[key] === undefined || params[key] === "" ? delete params[key] : {});
            self.getStudents('http://localhost:8000/students?' + $.param(params));
        });
    });

    Object.keys(self.findCourse).forEach(function (key) {
        self.findCourse[key].subscribe(function (val) {
            var params = ko.mapping.toJS(self.findCourse);
            Object.keys(params).forEach(key => params[key] === undefined || params[key] === "" ? delete params[key] : {});
            self.getCourses('http://localhost:8000/courses?' + $.param(params));
        });
    });

    Object.keys(self.findGrade).forEach(function (key) {
        self.findGrade[key].subscribe(function (val) {
            if (self.gradeMetadata.student()) {
                var params = ko.mapping.toJS(self.findGrade);
                Object.keys(params).forEach(key => params[key] === undefined || params[key] === "" ? delete params[key] : {});
                Object.keys(params).forEach(key => key === "course" ? params[key] = params[key].id : params[key]);

                self.grades.removeAll();
                $.ajax({
                    url: "http://localhost:8000" + self.gradeMetadata.studentlink + '/grades?' + $.param(params),
                    type: 'GET',
                    dataType: "json",
                }).done(function (result) {
                    result.forEach(function (object) {
                        var view = ko.mapping.fromJS(object, self);
                        ko.computed(function () {
                            return ko.mapping.toJSON(view);
                        }).subscribe(function (res) {
                            let resource = ko.mapping.fromJSON(res);
                            $.ajax({
                                url: "http://localhost:8000" + resource.link()[0].href(),
                                type: 'PUT',
                                dataType: "json",
                                contentType: "application/json",
                                data: ko.mapping.toJSON(view)
                            });
                        });
                        self.grades.push(view);
                    });
                });
            }
        });
    });
};

$(document).ready(function () {
    var model = new ViewModel();
    ko.applyBindings(model);
    model.getStudents('http://localhost:8000/students');
    model.getCourses('http://localhost:8000/courses');
    model.getCoursesSelect('http://localhost:8000/courses');
});