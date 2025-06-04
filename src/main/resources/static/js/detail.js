$(function(){
    const menuId = window.location.pathname.split('/').pop();
    let currentUser = null;

    // 1) 로그인 사용자
    $.get('/api/auth/me')
        .done(u => { currentUser = u; loadMenu(); loadReviews(); })
        .fail(() => { loadMenu(); loadReviews(); });

    // 2) 메뉴 정보 로드
    function loadMenu(){
        $.get(`/api/menus/${menuId}`)
            .done(menu => {
                $('#menuName').text(menu.name);
                $('#menuPrice').text(menu.price);
                $('#menuDescription').text(menu.description);
            })
            .fail(() => alert("메뉴 정보를 불러오지 못했습니다."));
    }

    // 3) 리뷰 목록 로드
    function loadReviews(){
        $.get(`/api/menus/${menuId}/reviews`)
            .done(reviews => {
                const $c = $('#reviews').empty();
                if (!reviews.length) {
                    $c.text('등록된 리뷰가 없습니다.');
                    return;
                }
                reviews.forEach(r => {
                    const imgTag = r.photoUrl
                        ? `<img src="${r.photoUrl}" width="120" class="block mt-2">`
                        : '';
                    let delBtn = '';
                    if (currentUser && r.userId === currentUser.id) {
                        delBtn = `<button data-id="${r.id}"
                                       class="delete-review-btn text-red-500 ml-2">
                                 삭제
                               </button>`;
                    }
                    const likeBtn = `<button data-id="${r.id}" class="like-review-btn text-blue-500 ml-2">좋아요</button>`;

                    $c.append(`
              <div class="border-b py-4">
                <div class="flex justify-between items-center">
                  <p class="text-sm">
                    <strong>${r.userName}</strong>
                    <span class="text-gray-500 ml-2 text-xs">${r.createdAt.replace('T',' ')}</span>
                    ${delBtn}
                  </p>
                </div>
            
                <div class="flex justify-between items-center mt-2">
                  <p class="text-sm">
                    점수: ${r.score}점 ${r.recommend ? '👍' : '👎'}
                    좋아요: <span class="like-count text-blue-700 font-semibold" data-id="${r.id}">${r.likeCount || 0}</span>
                  </p>
            
                  <button data-id="${r.id}" class="like-review-btn text-white text-xs font-medium px-3 py-1 rounded">
                    ❤️
                  </button>
                </div>
            
                ${imgTag}
                <p class="mt-2 text-sm">${r.content}</p>
              </div>
`);
                });
            })
            .fail(() => $('#reviews').text("리뷰를 불러오지 못했습니다."));
    }

    // 4) 리뷰 작성
    $('#reviewForm').submit(function(e){
        e.preventDefault();
        const formData = new FormData(this);
        if (currentUser) formData.set('userId', currentUser.id);

        $.ajax({
            url: `/api/menus/${menuId}/reviews`,
            method: 'POST',
            data: formData,
            processData: false,
            contentType: false
        }).done(() => {
            alert('리뷰 등록 완료');
            $('#reviewForm')[0].reset();
            loadReviews();
        }).fail(() => {
            alert('리뷰 등록 실패');
        });
    });

    // 5) 리뷰 삭제 핸들러
    $(document).on('click', '.delete-review-btn', function(){
        const reviewId = $(this).data('id');
        if (!confirm('리뷰를 삭제하시겠습니까?')) return;
        $.ajax({
            url: `/api/menus/${menuId}/reviews/${reviewId}`,
            method: 'DELETE'
        }).done(loadReviews)
            .fail(() => alert('삭제에 실패했습니다.'));
    });
    $(document).on('click', '.like-review-btn', function(){
        const reviewId = $(this).data('id');
        $.post(`/api/menus/${menuId}/reviews/${reviewId}/like`)
            .done(() => {
                // 해당 리뷰의 좋아요 수 증가
                const $count = $(`.like-count[data-id="${reviewId}"]`);
                $count.text(parseInt($count.text()) + 1);
            })
            .fail(() => alert('좋아요 실패'));
    });
});
