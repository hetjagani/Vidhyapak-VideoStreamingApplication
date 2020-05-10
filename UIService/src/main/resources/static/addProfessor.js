$('document').ready(function() {
    $('.table #editButton').on('click', function(event) {
        event.preventDefault();

        var href =  $(this).attr('href');
        $.get(href, function(pr, status) {
            console.log(pr);
            $('#proffIdEdit').val(pr.professorId);
            $('#personFNameEdit').val(pr.personFirstName);
            $('#personMNameEdit').val(pr.personMiddleName);
            $('#personLNameEdit').val(pr.personLastName);
            $('#personUNameEdit').val(pr.personUserName);
            $('#personPassEdit').val(pr.personPassword);
            $('#personConEdit').val(pr.personContactNumber);
            $('#personEmailEdit').val(pr.personEmail);
            $('#proffOfficeEdit').val(pr.professorOfficeNum);
            $('#professorDepartmentEdit').val(pr.professorDepartment);
            $('#professorDesignationEdit').val(pr.professorDesignation);

            var yyyymmdd = pr.personDOB.substr(0,10);
            $('#personDOBEdit').val(yyyymmdd);

            if(pr.personGender == 'male'){
                $('#inlineRadio1Edit').prop('checked', true);
            }else {
                $('#inlineRadio2Edit').prop('checked', true);
            }
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
