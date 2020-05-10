$('document').ready(function() {
    $('.row #videoDetails').on('click', function(event) {
        event.preventDefault();

        var href = $(this).attr('href');
        $.get(href, function(vid, status) {

            var baseAddr=href.split('/',1);
            $('.modal-body #watchVideo').attr('href', baseAddr + '/video/watch?id=' + vid.videoId);

            $('.modal-body #videoTitle').html(vid.videoTitle);
            $('.modal-body #videoDesc').html(vid.videoDescription);

            var courseDetailURL = "/courses/one?id=" + vid.courseId;
            $.get(courseDetailURL,function(c) {
                $('.modal-body #courseTitle').html(c.courseName + ' (' + c.courseCode + ')');
                $('.modal-body #courseSem').html(c.courseSemester + ' ' + c.courseYear);
            });

            $('.modal-body #lectureNotes').empty();
            vid.lectureNotes.forEach(function(ln, idx) {
                var lndownURL = "/lecturenote/download?id=" + ln.lectureNoteId;
                $.get(lndownURL, function(down){
                    var lnk = '<a href="'+ down +'">'+ ln.lectureNoteFileName +'</a> <br>';
                    $('.modal-body #lectureNotes').append(lnk);
                });
            });
        });

        $('#videoDetailsModal').modal();
    });
});