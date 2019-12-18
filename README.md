# mobileproject

## Start 11/15~20
-  제안서 작성

## 11/30 update
- Character.java commit
  - 마지막 회의에서 코딩 중 일부 변경 사항 - negativeExp 추가, 주기적으로 감소 루틴을 통해 적용 및 초기화
- MainActivity.java commit
  - dbhelper.class table record, id only 1, field name,height,weight,level,experience, character, calorie
  
## 12/04 update
- Charactr.java upgrade
  - 경험치 계산 함수 및 getter& setter 추가  
- FirstSetting.java commit
  - 설정값 MainActivity에 전달 구현
- MainActivity.java
  - 설정값 receive 함수 구현 중
  - Thread에서 주기적으로 값 설정하는 것 구현 중
  - 자세히 보기 등 각 기능 관련한 자료(시장조사) 필요 -> 레이아웃 구성 중

## 12/06 update
 - MainActivity.java
   - MainActivity.java에서 DBHelper 내부클래스 분할
   - Thread->Handler 정보변경 구현
   - 세부 구현 레이아웃 구현 중
 
  
## 12/17 update
 - MainActivity.java
   - 액티비티 간의 정보 전달 최소화, DB에 의한 설정 변경으로 이관
   - DB에 의해 각 액티비티마다 객체를 형성하도록 변경
     (Main - Character/ Detail - Character, Setting / AppSetting - setting / NoticeGoal - setting / Background - character, setting)
   - App 설정에 따라 갱신 주기 변경 가능하게 변경
   - 각 기능 Background 제외 세부 구현 완료
   - 이미지 세팅
   
 - FirstSetting.java
   - character 객체를 db에 효과적으로 전달하기 위한 생성
   - character(first-set) -> DB -> result -> character(main) 루트로 데이터 변경
   - 이미지 프로필 세팅
 
 - DetailScreen.java
   - 두 객체를 DB로부터 생성하여 주기적으로 변경하게 변경.
   - 이 액티비티가 실행되면, 메인 액티비티의 데몬 스레드가 종료되면서, 이 액티비티의 데몬 스레드가 실행되도록 구성
     (다시 메인으로 가면 반대로 실행)
 
 - NoticeGoal.java
   - setting의 객체로부터 데이터를 받아, 실행과 동시에 데이터가 입력상태가 되도록 함.
   - 수정 시 변경할 수 있음.
   - 초기화시 본래 객체 데이터로 되돌아감.
   
 - AppSetting.java
   - 위와 동일
   - 기록 삭제시 메인으로 돌아가 character의 객체 수정이 실행 되도록 함.
   - 캐릭터 삭제시 처음으로 되돌아가도록 편성함.
   
* * *
### 현재 수정 중
 - Background.java
   - service, 강제 종료 되더라도 새로 실행하도록 리턴
   - 적용 계산식 구현 중 (센서 ->> 성장도, 비만도 )
   - 스레드 3개 구현 중(메인 - 데이터 베이스 적용, 1 가상스레드 - 성장도, 2 가상스레드 - 비만도)

* * *
### 전달받아야 되는 것
- Notification.java
   - 대기 중
- Widget.java
   - 대기 중
   
## 12/19 update
 - MainActivity.java
   - 불필요한 기록 삭제 기능 삭제
   - 따라서 AppSetting의 레이아웃도 변경
 
 - background.java
   - 메인액티비티 terminated 이후 살아날 수 있도록 구성
   - 센서 합치기 완료
   
 - Widget.java
   - 합치기 중
   
 - Notification.java
   - 합치기 중
   
* * *

### Parsing 부분 관련
  - 시간 족
