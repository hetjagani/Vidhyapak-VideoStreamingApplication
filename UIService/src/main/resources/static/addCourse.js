$('document').ready(function() {
    $('.table #editButton').on('click', function(event) {
        event.preventDefault();

        var href =  $(this).attr('href');
        $.get(href, function(c, status) {
            $('#course_id_edit').val(c.courseId);
            $('#course_code_edit').val(c.courseCode);
            $('#course_name_edit').val(c.courseName);
            $('#course_year_edit').val(c.courseYear);
            $('#course_sem_edit').val(c.courseSemester);
            $('#course_type_edit').val(c.courseType);
        });

        $('#editModal').modal();
    });

    $('.table #deleteButton').on('click', function() {
        event.preventDefault();

        var href = $(this).attr('href');
        $('#deleteModal #delRef').attr('href', href);

        $('#deleteModal').modal();
    });
});