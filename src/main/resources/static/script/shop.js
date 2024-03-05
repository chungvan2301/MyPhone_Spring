document.addEventListener("DOMContentLoaded", function() {
// Lưu giá trị đã select
    let branchSelect = document.getElementById("branchSelect");
    let branch = document.getElementById("branch").value; //branch from server
    branchSelect.value = branch;

//Pagination
                let page = 0;
                const pageSize = 12;
                let sortField = (new URLSearchParams(window.location.search).get('sortField'))||0;
                let sortDirection = (new URLSearchParams(window.location.search).get('sortDirection'))||0;
                let categoryId = window.location.pathname.includes('/category/') ? window.location.pathname.split('/category/')[1] || 0 : 0;
                let productsSize = document.getElementById('productsSize').value;
                console.log(productsSize)
                if (productsSize<=12) {document.getElementById('load-more').style.visibility = 'hidden';}

                let statusProduct = document.getElementById('statusProduct').checked ? 1 :0;
                let minPrice = document.getElementById('minPrice').value;
                let maxPrice = document.getElementById('maxPrice').value;
                document.getElementById('load-more').addEventListener('click', function() {
                    page++;
                    fetch(`/shop/category/products/${categoryId}?page=${page}&size=${pageSize}&sortField=${sortField}&sortDirection=${sortDirection}&branch=${branch}&statusProduct=${statusProduct}&minPrice=${minPrice}&maxPrice=${maxPrice}`)
                        .then(response => response.text())
                        .then(data => {
                            let productList = document.getElementById('product-list');
                            productList.insertAdjacentHTML('beforeend', data);
                            $('input.rating').rating();
                            updateCountProducts();
                });
        });

                function updateCountProducts() {
                    let countProductsElement = document.getElementById('countProducts');
                    if (countProductsElement) {
                        document.getElementById('load-more').style.visibility = 'hidden';
                    } else {
                        console.log("còn sản phẩm");
                    }
                }
            });

