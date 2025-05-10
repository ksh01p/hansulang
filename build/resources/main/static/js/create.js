$(function(){
    $('#createMenuForm').submit(function(e){
        e.preventDefault();

        const formData = new FormData(this);

        $.ajax({
            url: '/api/menus',
            method: 'POST',
            data: formData,
            processData: false,
            contentType: false,
            success: function(){
                alert('메뉴 등록 성공');
                location.href = '/board/list';
            },
            error: function(){
                alert('메뉴 등록 실패');
            }
        });
    });
});
