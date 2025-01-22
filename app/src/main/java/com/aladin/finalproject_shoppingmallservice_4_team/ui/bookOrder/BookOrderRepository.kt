package com.aladin.finalproject_shoppingmallservice_4_team.ui.bookOrder


import com.aladin.apiTestApplication.network.AladdinApiService
import com.aladin.finalproject_shoppingmallservice_4_team.BuildConfig
import com.aladin.finalproject_shoppingmallservice_4_team.model.OrderInquiryModel
import com.aladin.finalproject_shoppingmallservice_4_team.model.ShoppingCartModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BookOrderRepository @Inject constructor(
    private val apiService: AladdinApiService,
    private val firebaseFireStore: FirebaseFirestore
) {

    // 주문 데이터 가져오기
    suspend fun gettingOrderInquiryDataFromFirBaseStore(
        userToken: String,
        userBuyTime: Long
    ): List<OrderInquiryModel> {
        val collectionReference = firebaseFireStore.collection("OrderInquiryTable")

        // 사용자 토큰값 및 구매 시간에 따라 데이터를 가져온다.
        val querySnapshot: QuerySnapshot = collectionReference
            .whereEqualTo("orderInquiryUserToken", userToken) // 사용자 토큰값과 비교하여 같은 정보들만 들고온다.
            .whereEqualTo("orderInquiryTime", userBuyTime) // 같은 구매시간의 아이템정보들만 가져온다.
            .get()
            .await()

        val userInquiryModel = querySnapshot.documents.mapNotNull { document ->
            document.toObject(OrderInquiryModel::class.java)
        }

        return userInquiryModel
    }


    // 장바구니 데이터 가져오기
    suspend fun gettingOrderBookDataFromFireBaseStore(userToken: String): Pair<List<ShoppingCartModel>, Triple<List<String>, List<String>, List<String>>> {
        val collectionReference = firebaseFireStore.collection("ShoppingCartTable")

        // 장바구니에 있는 책들만 필터링
        val querySnapshot: QuerySnapshot = collectionReference
            .whereEqualTo("shoppingCartUserToken", userToken) // 사용자 토큰값과 비교하여 같은 정보들만 들고온다.
            .whereEqualTo("shoppingCartState", 1) // 1인 상태값들만 가져온다.
            .get()
            .await()

        // 쿼리 결과에서 ShoppingCartModel을 추출
        val bookList = querySnapshot.documents.mapNotNull { document ->
            document.toObject(ShoppingCartModel::class.java)
        }

        // 가져온 문서들의 shoppingCartState 값을 0으로 업데이트
        querySnapshot.documents.forEach { document ->
            document.reference.update("shoppingCartState", 0)
        }

        // 쿼리 결과에서 ISBN 리스트 추출
        val shoppingCartBookISBNList = bookList.mapNotNull { it.shoppingCartISBN }

        // ISBN 리스트를 사용해 API 호출하여 책 표지 이미지, 책 제목, 저자 추출
        val coverList = gettingOrderBookTripleData(shoppingCartBookISBNList)

        // Pair로 shoppingCartList와 Triple 책 데이터 반환
        return Pair(bookList, coverList)
    }

    // 책 검색 API 호출 함수
    suspend fun gettingOrderBookTripleData(queries: List<String>): Triple<List<String>, List<String>, List<String>> {
        val shoppingCartBookCovers = mutableListOf<String>()
        val shoppingCartBookTitles = mutableListOf<String>()
        val shoppingCartBookAuthors = mutableListOf<String>()

        for (query in queries) {
            // API 호출
            val response = apiService.searchBooks(BuildConfig.API_KEY, query)

            // 각 항목에서 필요한 정보를 추출하여 리스트에 추가
            response.items.forEach { item ->
                shoppingCartBookCovers.add(item.cover)
                shoppingCartBookTitles.add(item.title)
                shoppingCartBookAuthors.add(item.author)
            }
        }

        // cover, title, author 리스트를 Triple로 반환
        return Triple(shoppingCartBookCovers, shoppingCartBookTitles, shoppingCartBookAuthors)
    }

    // 책 구매 정보 저장
    suspend fun saveShoppingCartDataToOrderInquiryTable(
        userOrderBookList: List<OrderInquiryModel>
    ) {
        val shoppingCartTableReference = firebaseFireStore.collection("ShoppingCartTable")
        val orderInquiryTableReference = firebaseFireStore.collection("OrderInquiryTable")
        val usedBookInventoryTableReference = firebaseFireStore.collection("UsedBookInventoryTable")
        val bookCountTableReference = firebaseFireStore.collection("BookCountTable")

        // orderInquiryTable에 데이터 추가
        userOrderBookList.forEach { order ->
            // 데이터를 FireStore에 추가
            orderInquiryTableReference.add(order).await()

            // 해당 ISBN과 품질을 만족하는 데이터 필터링
            val querySnapshot = usedBookInventoryTableReference
                .whereEqualTo("usedBookISBN", order.orderInquiryISBN)
                .whereEqualTo("usedBookQuality", order.orderInquiryQuality)
                .get()
                .await()

            // querySnapshot에서 usedBookTime이 가장 작은 데이터 찾기
            val minTimeDocument = querySnapshot.documents.minByOrNull { document ->
                document.getLong("usedBookTime") ?: Long.MAX_VALUE
            }

            // 가장 작은 usedBookTime 값을 가진 데이터를 찾은 경우
            minTimeDocument?.let {
                // 상태 변경: usedBookState를 값에 맞게 업데이트
                val documentRef = usedBookInventoryTableReference.document(it.id)
                documentRef.update("usedBookState", 1).await() // 상태를 "sold"로 업데이트
            }
        }

        // 장바구니에서 구매한 아이템 삭제
        userOrderBookList.forEach { item ->
            val userToken = item.orderInquiryUserToken
            val isbn = item.orderInquiryISBN

            // Firestore에서 해당 문서 가져오기
            val shoppingCartTableDocuments = shoppingCartTableReference
                .whereEqualTo("shoppingCartUserToken", userToken)
                .whereEqualTo("shoppingCartISBN", isbn)
                .get()
                .await()

            for (document in shoppingCartTableDocuments) {
                // ShoppingCartState 값을 1로 업데이트
                document.reference.delete().await()
            }
        }

        // BookCountTable에서 총 수량 감소 및 상태 변경
        userOrderBookList.forEach { order ->
            val isbn = order.orderInquiryISBN
            val orderQuality = order.orderInquiryQuality

            // BookCountTable에서 해당 ISBN의 책 데이터를 가져오기
            val bookCountDocumentSnapshot = bookCountTableReference
                .whereEqualTo("bookCountISBN", isbn)
                .whereEqualTo("bookCountQuality", orderQuality)
                .get()
                .await()

            // 해당 ISBN의 bookCountTotal을 업데이트
            bookCountDocumentSnapshot.documents.firstOrNull()?.let { document ->
                val currentTotal = document.getLong("bookCountTotal") ?: 0

                // 재고 차감 후 새로운 값 계산
                val newTotal = currentTotal - orderQuality

                if (newTotal <= 0) {
                    // 재고가 0 이하인 경우 bookCountState 상태를 "1(품절)" 로으로 변경
                    document.reference.update("bookCountTotal", 0, "bookCountState", 1).await()
                } else {
                    // 재고가 남아 있는 경우 bookCountTotal만 업데이트
                    document.reference.update("bookCountTotal", newTotal).await()
                }
            }
        }
    }
}