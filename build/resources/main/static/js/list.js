// src/main/resources/static/js/list.js
$(function(){
    let currentUser = null;
    let allMenus = [];         // 서버에서 받아온 전체 메뉴
    let filteredMenus = [];    // 검색·필터링된 메뉴들

    // 1) 현재 로그인 사용자 정보 조회
    $.get('/api/auth/me')
        .done(u => { currentUser = u; })
        .always(() => {
            // 로그인 여부 상관없이 메뉴 로드
            loadMenus();
        });

    // 2) 메뉴 리스트 로드 (기본 정렬: 식당 오름차순)
    function loadMenus(sortBy = 'restaurant', direction = 'ASC') {
        // 로딩 스피너 표시
        $('#emptyState').hide();
        $('#menuTableBody').empty();
        $('#loadingSpinner').removeClass('hidden');

        $.ajax({
            url: `/api/menus?sortBy=${sortBy}&direction=${direction}`,
            method: 'GET'
        })
            .done(menus => {
                allMenus = menus;          // 서버에서 받은 전체 데이터 저장
                filteredMenus = menus;     // 초기에는 그대로 다 보여줌
                renderMenus();             // 테이블에 반영
            })
            .fail(() => {
                alert("메뉴 목록을 불러오는 데 실패했습니다.");
            })
            .always(() => {
                // 로딩 완료 후 스피너 감춤
                $('#loadingSpinner').addClass('hidden');
            });
    }

    // 3) 메뉴 목록을 테이블 행으로 렌더링
    function renderMenus() {
        const $tbody = $('#menuTableBody').empty();

        if (filteredMenus.length === 0) {
            // 메뉴가 없거나, 검색·필터 결과가 하나도 없을 때 빈 상태 노출
            $('#emptyState').removeClass('hidden');
            return;
        } else {
            $('#emptyState').addClass('hidden');
        }

        filteredMenus.forEach(menu => {
            // 가격, 평점 포맷팅
            const priceText = menu.price.toLocaleString() + '원';
            const avgScoreText = menu.avgScore.toFixed(1) + '점';
            const descText = menu.description || '';
            const truncatedDesc = descText.length > 30
                ? descText.substring(0, 30) + '…'
                : descText;

            // 작업 버튼 (리뷰쓰기, 삭제)
            let actionButtons = `
        <a href="/board/detail/${menu.id}"
           class="inline-block text-indigo-600 hover:underline text-sm mr-2">
          리뷰 쓰기
        </a>`;

            if (currentUser && menu.createdById === currentUser.id) {
                actionButtons += `
          <button data-id="${menu.id}"
                  class="delete-menu-btn text-red-500 hover:underline text-sm">
            삭제
          </button>`;
            }

            // <tr> 한 줄 생성
            const $tr = $(`
        <tr class="bg-white hover:bg-gray-50 transition">
          <!-- 이미지 컬럼은 제외했습니다 -->
          <td class="px-4 py-3 text-sm text-gray-800">${menu.name}</td>
          <td class="px-4 py-3 text-sm text-gray-800">${priceText}</td>
          <td class="px-4 py-3 text-sm text-gray-800">${menu.restaurant}</td>
          <td class="px-4 py-3 text-sm text-gray-800 text-center">${menu.reviewCount}</td>
          <td class="px-4 py-3 text-sm text-gray-800 text-center">${avgScoreText}</td>
          <td class="px-4 py-3 text-sm text-gray-700 truncate w-48">${truncatedDesc}</td>
          <td class="px-4 py-3 text-sm text-center">${actionButtons}</td>
        </tr>
      `);

            $tbody.append($tr);
        });
    }

    // 4) 메뉴 삭제 핸들러 (동적 바인딩)
    $(document).on('click', '.delete-menu-btn', function(){
        const id = $(this).data('id');
        if (!confirm('정말 메뉴를 삭제하시겠습니까?')) return;

        $.ajax({
            url: `/api/menus/${id}`,
            method: 'DELETE'
        }).done(() => {
            // 삭제 성공 시, 로컬 데이터에서 제거 후 다시 렌더링
            allMenus = allMenus.filter(m => m.id !== id);
            filteredMenus = filteredMenus.filter(m => m.id !== id);
            renderMenus();
        }).fail(() => alert('삭제에 실패했습니다.'));
    });

    // 5) 정렬 적용 버튼 클릭 시
    $('#sortBtn').on('click', function(){
        const [sortBy, direction] = $('#sortSelect').val().split(',');
        loadMenus(sortBy, direction);
    });

    // 6) 검색 입력(input) 키 입력 시: 클라이언트 측 필터링
    $('#searchInput').on('input', function(){
        const keyword = $(this).val().trim().toLowerCase();
        if (!keyword) {
            filteredMenus = allMenus.slice();
        } else {
            filteredMenus = allMenus.filter(menu =>
                menu.name.toLowerCase().includes(keyword) ||
                menu.restaurant.toLowerCase().includes(keyword)
            );
        }
        renderMenus();
    });

});
