$(function(){
    // URL에서 마지막 segment로 menuId 추출 (/board/detail/{id})
    const menuId = window.location.pathname.split('/').pop();

    if (!menuId) {
        alert("메뉴 ID가 없습니다.");
        return;
    }

    // 1) 메뉴 정보 불러오기
    $.get(`/api/menus/${menuId}`, function(menu){
        $('#menuName').text(menu.name);
        $('#menuPrice').text(menu.price);
        $('#menuDescription').text(menu.description);
    }).fail(() => {
        alert("메뉴 정보를 불러오지 못했습니다.");
    });

    // 2) 리뷰 목록 불러오기
    function loadReviews(){
        $.get(`/api/menus/${menuId}/reviews`, function(list){
            const container = $('#reviews').empty();
            if (list.length === 0) {
                container.text('등록된 리뷰가 없습니다.');
            } else {
                list.forEach(r => {
                    container.append(`
                        <div class="mb-4">
                            <p><strong>${r.title}</strong> (${r.score}점) - ${r.recommend ? '👍' : '👎'}</p>
                            <p>${r.content}</p>
                            <hr/>
                        </div>
                    `);
                });
            }
        }).fail(() => {
            $('#reviews').text("리뷰를 불러오지 못했습니다.");
        });
    }

    loadReviews();

    // 3) 리뷰 작성
    $('#reviewForm').submit(function(e){
        e.preventDefault();
        const rev = {
            userName: $('input[name=userName]').val(),
            score: parseInt($('select[name=score]').val()),
            title: $('input[name=title]').val(),
            content: $('textarea[name=content]').val(),
            recommend: $('input[name=recommend]:checked').val() === 'true'
        };

        $.ajax({
            url: `/api/menus/${menuId}/reviews`,
            method: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(rev),
            success: function(){
                alert('리뷰 등록 완료');
                $('#reviewForm')[0].reset();
                loadReviews();
            },
            error: function(){
                alert('리뷰 등록 실패');
            }
        });
    });
});
