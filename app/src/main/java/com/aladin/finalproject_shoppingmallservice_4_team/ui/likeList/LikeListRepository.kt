package com.aladin.finalproject_shoppingmallservice_4_team.ui.likeList

import com.aladin.apiTestApplication.network.AladdinApiService
import com.aladin.finalproject_shoppingmallservice_4_team.BuildConfig
import com.aladin.finalproject_shoppingmallservice_4_team.model.LikeListModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LikeListRepository @Inject constructor(
    private val apiService: AladdinApiService,
    private val firebaseFireStore: FirebaseFirestore
) {

    suspend fun gettingLikeListDataFromFireBaseStore(userToken: String): Pair<List<LikeListModel>, Quadruple<List<String>, List<String>, List<String>, List<Int>>> {
        val collectionReference = firebaseFireStore.collection("LikeListTable")

        // 장바구니에 있는 책들만 필터링
        val querySnapshot: QuerySnapshot = collectionReference
            .whereEqualTo("likeListUserToken", userToken) // 사용자 토큰값과 비교하여 같은 정보들만 들고온다.
            .whereEqualTo("likeListState", 0) // 상태값 0(정상)인것들만 가져온다.
            .get()
            .await()

        // 쿼리 결과에서 ShoppingCartModel을 추출
        val likeListBookList = querySnapshot.documents.mapNotNull { document ->
            document.toObject(LikeListModel::class.java)
        }

        // 쿼리 결과에서 ISBN 리스트 추출
        val shoppingCartBookISBNList = likeListBookList.mapNotNull { it.likeListISBN }

        // ISBN 리스트를 사용해 API 호출하여 책 표지 이미지, 책 제목, 저자 추출
        val coverList = gettingShoppingCartBookQuadrupleData(shoppingCartBookISBNList)

        // Pair로 shoppingCartList와 Triple 책 데이터 반환
        return Pair(likeListBookList, coverList)
    }

    // 책 검색 API 호출 함수
    // Quadruple 클래스를 사용하여 네 개의 리스트 반환
    data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)

    suspend fun gettingShoppingCartBookQuadrupleData(queries: List<String>): Quadruple<List<String>, List<String>, List<String>, List<Int>> {
        val likeListBookCovers = mutableListOf<String>()
        val likeListBookTitles = mutableListOf<String>()
        val likeListBookAuthors = mutableListOf<String>()
        val likeListBookSellPrice = mutableListOf<Int>()

        for (query in queries) {
            // API 호출
            val response = apiService.searchBooks(BuildConfig.API_KEY, query)

            // 각 항목에서 필요한 정보를 추출하여 리스트에 추가
            response.items.forEach { item ->
                likeListBookCovers.add(item.cover)
                likeListBookTitles.add(item.title)
                likeListBookAuthors.add(item.author)
                likeListBookSellPrice.add(item.priceStandard)
            }
        }

        // cover, title, author, sellPrice 리스트를 Quadruple로 반환
        return Quadruple(likeListBookCovers, likeListBookTitles, likeListBookAuthors, likeListBookSellPrice)
    }

    // 찜목록 리스트 삭제 (State값만 1로 변경)
    suspend fun deleteLikeListData(userToken: String, isbnNumber: String) {
        val collectionReference = firebaseFireStore.collection("LikeListTable")

        // 장바구니에 있는 책들만 필터링
        val querySnapshot: QuerySnapshot = collectionReference
            .whereEqualTo("likeListUserToken", userToken) // 사용자 토큰값과 비교하여 같은 정보들만 들고온다.
            .whereEqualTo("likeListISBN", isbnNumber) // 책의 ISBN으로 데이터 하나만 가져온다.
            .whereEqualTo("likeListState", 0) // 상태값 0(정상)인것들만 가져온다.
            .get()
            .await()

        // 해당 문서가 존재하면 삭제 (하나의 아이템만 있을 경우)
        querySnapshot.documents.firstOrNull()?.let { document ->
            collectionReference.document(document.id).update("likeListState",1).await()
        }
    }
}