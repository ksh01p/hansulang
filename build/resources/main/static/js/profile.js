$(function () {
    let currentUser = null;

    // 1) 로그인 사용자 정보 조회
    $.get('/api/auth/me')
        .done(user => {
            currentUser = user;
            loadUserPosts();
            loadUserComments();
        })
        .fail(() => {
            $('#user-posts').html('<p class="text-gray-500">로그인 정보가 없습니다.</p>');
            $('#user-comments').html('<p class="text-gray-500">로그인 정보가 없습니다.</p>');
        });

    // 2) 게시글 로드
    function loadUserPosts() {
        $.get('/api/user/posts')
            .done(posts => {
                const $postList = $('#user-posts').empty();
                if (!posts.length) {
                    $postList.append('<p class="text-gray-500">작성한 게시글이 없습니다.</p>');
                    return;
                }
                posts.forEach(post => {
                    $postList.append(`
                        <li>
                            <a href="/board/detail/${post.id}" class="text-blue-600 hover:underline">
                                ${post.title}
                            </a>
                        </li>
                    `);
                });
            })
            .fail(() => {
                $('#user-posts').html('<p class="text-red-500">게시글 로딩 실패</p>');
            });
    }

    // 3) 댓글 로드
    function loadUserComments() {
        $.get('/api/user/comments')
            .done(comments => {
                const $commentList = $('#user-comments').empty();
                if (!comments.length) {
                    $commentList.append('<p class="text-gray-500">작성한 댓글이 없습니다.</p>');
                    return;
                }
                comments.forEach(comment => {
                    $commentList.append(`
                        <li class="mb-2">
                            <p class="text-gray-800">${comment.content}</p>
                            <p class="text-sm text-gray-400">
                                on <a href="/board/detail/${comment.postId}" class="underline">게시글 보기</a>
                            </p>
                        </li>
                    `);
                });
            })
            .fail(() => {
                $('#user-comments').html('<p class="text-red-500">댓글 로딩 실패</p>');
            });
    }
});
