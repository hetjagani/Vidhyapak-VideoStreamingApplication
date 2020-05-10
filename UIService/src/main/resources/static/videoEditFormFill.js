$('document').ready(function() {
    $('.table #editButton').on('click', function(event) {
        event.preventDefault();

        var href =  $(this).attr('href');
        $.get(href, function(video, status) {
            $('#video-id-edit').val(video.videoId);
            $('#video-courseId-edit').val(video.courseId);
            $('#video-title-edit').val(video.videoTitle);
            $('#video-desc-edit').val(video.videoDescription);
        });

        $('#editModal').modal();
    });

    $('.table #deleteButton').on('click', function() {
        event.preventDefault();

        var href = $(this).attr('href');
        $('#deleteModal #delVid').attr('href', href);

        $('#deleteModal').modal();
    });
});