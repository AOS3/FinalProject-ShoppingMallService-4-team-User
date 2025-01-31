package com.aladin.finalproject_shoppingmallservice_4_team.ui.shoppingcart

import com.aladin.apiTestApplication.network.AladdinApiService
import com.aladin.finalproject_shoppingmallservice_4_team.BuildConfig
import com.aladin.finalproject_shoppingmallservice_4_team.model.ShoppingCartModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ShoppingCartRepository @Inject constructor(
    private val apiService: AladdinApiService,
    private val firebaseFireStore: FirebaseFirestore
) {

    suspend fun changeShoppingCartStateToOne(buyList: List<Triple<String, String,Int>>) {
        val collectionReference = firebaseFireStore.collection("ShoppingCartTable")

        for (item in buyList) {
            val userToken = item.first
            val isbn = item.second
            val quality = item.third

            // Firestore에서 해당 문서 가져오기
            val documents = collectionReference
                .whereEqualTo("shoppingCartUserToken", userToken)
                .whereEqualTo("shoppingCartISBN", isbn)
                .whereEqualTo("shoppingCartISBN", quality)
                .get()
                .await()

            for (document in documents) {
                // ShoppingCartState 값을 1로 업데이트
                document.reference.update("shoppingCartState", 1).await()
            }
        }
    }

    suspend fun gettingBookDataFromFireBaseStore(userToken: String): Pair<List<ShoppingCartModel>, Triple<List<String>, List<String>, List<String>>> {
        val collectionReference = firebaseFireStore.collection("ShoppingCartTable")

        // 장바구니에 있는 책들만 필터링
        val querySnapshot: QuerySnapshot = collectionReference
            .whereEqualTo("shoppingCartUserToken", userToken) // 사용자 토큰값과 비교하여 같은 정보들만 들고온다.
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
        val coverList = gettingShoppingCartBookTripleData(shoppingCartBookISBNList)

        // Pair로 shoppingCartList와 Triple 책 데이터 반환
        return Pair(bookList, coverList)
    }

    // 책 검색 API 호출 함수
    suspend fun gettingShoppingCartBookTripleData(queries: List<String>): Triple<List<String>, List<String>, List<String>> {
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

    // 리스트 삭제
    suspend fun deleteShoppingCartBookData(deleteList: List<Triple<String, String,Int>>): Boolean {
        val collectionReference = firebaseFireStore.collection("ShoppingCartTable")

        for (item in deleteList) {
            val userToken = item.first  // userToken
            val isbn = item.second      // ISBN
            val quality = item.third

            // Firestore에서 userToken과 ISBN에 해당하는 문서 찾기
            val querySnapshot = collectionReference
                .whereEqualTo("shoppingCartUserToken", userToken)
                .whereEqualTo("shoppingCartISBN", isbn)
                .whereEqualTo("shoppingCartQuality", quality)
                .get()
                .await()

            // 해당 문서가 존재하면 삭제
            for (document in querySnapshot.documents) {
                collectionReference.document(document.id).delete().await()
            }
        }
        return true
    }
}