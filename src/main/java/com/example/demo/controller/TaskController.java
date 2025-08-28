package com.example.demo.controller;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.entity.Category;
import com.example.demo.entity.Tasks;
import com.example.demo.model.Account;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.TasksRepository;

/**
 * タスクの表示、追加、更新、削除を管理するコントローラークラス。
 */
@Controller
public class TaskController {
    
    @Autowired
    Account account;
    
    @Autowired
    CategoryRepository categoryRepository;
    
    @Autowired
    TasksRepository tasksRepository;
    
    /**
     * タスクの一覧ページを表示する。
     * 検索、フィルタリング、並べ替え、ページネーション、グラフデータの表示をサポートする。
     * @param keyword 検索キーワード
     * @param categoryId カテゴリID
     * @param isTrash ゴミ箱内のタスクを表示するか
     * @param sortByClosingDate 期限日で並べ替えるか
     * @param progress 進捗状況
     * @param filterGraphArea グラフ表示期間（週:0, 月:1）
     * @param targetWeekOrMonth グラフ表示期間の移動量（週または月）
     * @param pageable ページネーション情報
     * @param model ビューに渡すためのデータモデル
     * @return tasks.html
     */
    @GetMapping("/tasks")
    public String index(
            @RequestParam(name = "keyword", defaultValue = "") String keyword,
            @RequestParam(name = "categoryId", required = false) Integer categoryId,
            @RequestParam(name = "trash", defaultValue = "false") String isTrash,
            @RequestParam(name = "sortByClosingDate", defaultValue = "false") String sortByClosingDate,
            @RequestParam(name = "progress", required = false) Integer progress,
            @RequestParam(name = "filterGraphArea", required = false) Integer filterGraphArea,
            @RequestParam(name = "targetWeekOrMonth", required = false) Integer targetWeekOrMonth,
            Pageable pageable,
            Model model) {
        
        String graphMsg = "達成比率";
        // グラフの期間を決定 (0:週, 1:月)
        boolean isWeek = (filterGraphArea == null || filterGraphArea == 0);
        
        
        if(account.isNeedGraph()) {
            // 前後週/月の移動量を決定
            if (targetWeekOrMonth == null) {
                targetWeekOrMonth = 0;
            }
            
            // グラフのタイトルメッセージを決めるためのロジック
            if(isWeek) {
                // 週
                if(targetWeekOrMonth == null || targetWeekOrMonth == 0) {
                    graphMsg = "今週の週間" + graphMsg; 
                }else if(targetWeekOrMonth > 0){
                    if(targetWeekOrMonth == 1) {
                        graphMsg = "翌週の週間" + graphMsg;
                    }else {
                        graphMsg = Math.abs(targetWeekOrMonth) + "週間後の週間" + graphMsg;
                    }

                }else{
                    if(targetWeekOrMonth == -1) {
                        graphMsg = "先週の週間" + graphMsg;
                    }else {
                        graphMsg = Math.abs(targetWeekOrMonth) + "週間前の週間" + graphMsg;
                    }
                }
            }else {
               // 月
                if(targetWeekOrMonth == null || targetWeekOrMonth == 0) {
                    graphMsg = "今月の月間" + graphMsg; 
                }else if(targetWeekOrMonth > 0){
                    if(targetWeekOrMonth == 1) {
                        graphMsg = "翌月の月間" + graphMsg;
                    }else {
                        graphMsg = Math.abs(targetWeekOrMonth) + "か月後の月間" + graphMsg;
                    }

                }else{
                    if(targetWeekOrMonth == -1) {
                        graphMsg = "先月の月間" + graphMsg;
                    }else {
                        graphMsg = Math.abs(targetWeekOrMonth) + "か月前の月間" + graphMsg;
                    }
                }
            }
            
            if(progress != null) {
                graphMsg += "\n [";
                switch(progress){
                    case 0:
                        graphMsg += "未着手";
                        break;
                    case 1:
                        graphMsg += "進行中";
                        break;
                    case 2:
                        graphMsg += "完了";
                        break;
                }
                
                graphMsg += "]";
            }
        }

        
        // Page形式でタスクを取得
        Page<Tasks> pages = null;

        boolean isCreated = true;
        
        //フィルタリングされたタスクリストを取得
        if (keyword != null && !keyword.isEmpty() && "true".equals(isTrash)) {
            pages = tasksRepository.findByIdWithKeyword(keyword, account.getId(), pageable);
        }else if (keyword != null && !keyword.isEmpty() && !"true".equals(isTrash)) {
            pages = tasksRepository.findByIdWithKeyword(keyword, account.getId(), pageable);
        } else if (categoryId != null) {
            pages = tasksRepository.findByIdAndCategoryId(categoryId, account.getId(),pageable);
        } else if ("true".equals(isTrash)) {
            pages = tasksRepository.findAllDeleted(account.getId(), pageable);
        } else if ("true".equals(sortByClosingDate)) {
            pages = tasksRepository.findAllByIdAscClosingDate(account.getId(), pageable);
        } else if (progress != null && progress >= 0 && progress <= 2) {
            pages = tasksRepository.findByProgress(progress, account.getId(), pageable);
        } else {
            pages = tasksRepository.findAllById(account.getId(), pageable);
            if(pages.isEmpty()) {
                isCreated = false;
            }
        }
        
        List<Tasks> tasks = pages.getContent();

        
        // カテゴリのMapとリストを準備
        Map<Integer, String> categoriesMap = categoryRepository.findAll().stream()
                .collect(Collectors.toMap(Category::getId, Category::getName));
        List<Category> categories = categoryRepository.findAll();
        
        // グラフデータの初期化
        Integer[] data = new Integer[0];
        Integer[][] dataSet = new Integer[0][];
        LocalDate[] label = new LocalDate[0];
    
        if(account.isNeedGraph()) {
            try {
                if (categoryId != null) {
                    // カテゴリid事
                    dataSet = new Integer[3][];
                    for (int i = 0; i < 3; i++) {
                        Map<LocalDate, Integer> graphData = isWeek ?
                            getGraphDataWeek(targetWeekOrMonth, i, categoryId) :
                            getGraphDataMonth(targetWeekOrMonth, i, categoryId);
                        
                        if (label.length == 0) {
                            label = graphData.keySet().toArray(new LocalDate[0]);
                        }
                        dataSet[i] = graphData.values().toArray(new Integer[0]);
                    }
                }
                
                else if (progress != null) {                     
                    // 特定の進捗状況のグラフデータを取得
                    Map<LocalDate, Integer> graphData = isWeek ?
                        getGraphDataWeek(targetWeekOrMonth, progress) :
                        getGraphDataMonth(targetWeekOrMonth, progress);
                    
                    label = graphData.keySet().toArray(new LocalDate[0]);
                    data = graphData.values().toArray(new Integer[0]);
                }else {
                   // すべての進捗状況のグラフデータを取得
                    dataSet = new Integer[3][];
                    for (int i = 0; i < 3; i++) {
                        Map<LocalDate, Integer> graphData = isWeek ?
                            getGraphDataWeek(targetWeekOrMonth, i) :
                            getGraphDataMonth(targetWeekOrMonth, i);
                        
                        if (label.length == 0) {
                            label = graphData.keySet().toArray(new LocalDate[0]);
                        }
                        dataSet[i] = graphData.values().toArray(new Integer[0]);
                    }  
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("グラフデータの生成中にエラーが発生しました。");
            }
        }
        
        // モデルにデータを追加
        model.addAttribute("categories", categories);
        model.addAttribute("graphMsg", graphMsg);
        model.addAttribute("categoriesMap", categoriesMap);
        model.addAttribute("keyword", keyword);         
        
        model.addAttribute("tasks", tasks);
        model.addAttribute("pages", pages);
        
        model.addAttribute("isTrash", isTrash);
        model.addAttribute("isCreated", isCreated);
        model.addAttribute("label", label);
        model.addAttribute("data", data);
        model.addAttribute("dataSet", dataSet);
        model.addAttribute("progress", progress);
        model.addAttribute("targetWeekOrMonth", targetWeekOrMonth);
        model.addAttribute("filterGraphArea", filterGraphArea);
        
        return "tasks";
    }
    
    /**
     * 新しいタスクを追加する。
     * @PostMappingと@ResponseBodyを使用してJSON形式でレスポンスを返す。
     * @param categoryId カテゴリID
     * @param title タスクのタイトル
     * @param memo タスクのメモ
     * @param closingDate タスクの期限日
     * @return 成功/失敗を示すJSONレスポンス
     */
    @PostMapping("/tasks/add")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> store(@RequestParam(name = "categoryId", required = false) Integer categoryId,
            @RequestParam(name = "title", defaultValue = "")String title,
            @RequestParam(name = "memo", defaultValue = "")String memo,
            @RequestParam(name = "closingDate", required = false)LocalDate closingDate
            ){
                
        List<String> errList = new ArrayList<>();
        
        if(title == null || !(title.length() > 0)) {
            errList.add("名前を入力してください");
        }
        if(memo == null || !(memo.length() > 0)) {
            errList.add("メモを入力してください");
        }
        
        if(closingDate == null) {
            errList.add("期限を入力してください");
        }
        
        // 疑似的にResponseを作成
        Map<String, Object> response = new HashMap<>();
        
        if (errList.size() > 0) {
            // エラーがある場合、400 Bad Requestとエラーリストを返す
            response.put("success", false);
            response.put("errList", errList);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } else {
            // 成功した場合、200 OKと成功メッセージを返す
            tasksRepository.save(new Tasks(categoryId, account.getId(), title, memo, closingDate));
            response.put("success", true);
            response.put("message", "タスクが正常に追加されました");
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
    }
    
    /**
     * 既存のタスクを更新する。
     * @PostMappingと@ResponseBodyを使用してJSON形式でレスポンスを返す。
     * @param id 更新するタスクのID
     * @param categoryId カテゴリID
     * @param title タスクのタイトル
     * @param memo タスクのメモ
     * @param closingDate タスクの期限日
     * @param progress 進捗状況
     * @return 成功/失敗を示すJSONレスポンス
     */
    @PostMapping("/tasks/edit/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> update(@PathVariable("id") Integer id,
            @RequestParam(name = "categoryId", required = false) Integer categoryId,
            @RequestParam(name = "title", defaultValue = "")String title,
            @RequestParam(name = "memo", defaultValue = "")String memo,
            @RequestParam(name = "closingDate", required = false)LocalDate closingDate,
            @RequestParam(name = "progress", required = false)Integer progress
            ){

        Optional<Tasks> taskOp = tasksRepository.findById(id);
        // 疑似的にResponseを作成
        Map<String, Object> response = new HashMap<>();

        // タスクが存在しない場合はエラー
        if(!taskOp.isPresent()) {
            response.put("success", false);
            response.put("errList", List.of("タスクが存在しません"));
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        // 既存のタスクオブジェクトを取得
        Tasks task = taskOp.get();

        // バリデーション
        List<String> errList = new ArrayList<>();
        if(title == null || !(title.length() > 0)) {
            errList.add("名前を入力してください");
        }
        if(memo == null || !(memo.length() > 0)) {
            errList.add("メモを入力してください");
        }
        
        if(closingDate == null) {
            errList.add("期限を入力してください");
        }

        // progressの値が不正でないかチェック
        List<Category> categories = categoryRepository.findAll();
        if(progress == null || progress < 0 || progress >= categories.size()) {
            errList.add("カテゴリに不正な値が入力されました");
        }

        if (errList.size() > 0) {
            // エラーがある場合、400 Bad Requestとエラーリストを返す
            response.put("success", false);
            response.put("errList", errList);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } else {
            // 成功した場合、200 OKと成功メッセージを返す
        
            // 既存のタスクのプロパティを更新
            task.setCategoryId(categoryId);
            task.setTitle(title);
            task.setMemo(memo);
            task.setClosingDate(closingDate);
            task.setProgress(progress);

            tasksRepository.save(task);
            
            response.put("success", true);
            response.put("message", "タスクが正常に更新されました");
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
    }
    
    /**
     * タスクを論理削除する（ゴミ箱に入れる）。
     * @param id 論理削除するタスクのID
     * @return タスク一覧ページにリダイレクト
     */
    @PostMapping("/tasks/delete/{id}")
    public String delete(@PathVariable("id") Integer id){
        Optional<Tasks> taskOp = tasksRepository.findById(id);
        Tasks task = null;
        
        List<String> errList = new ArrayList<>();
        
        if(!taskOp.isPresent()) {
            errList.add("タスクが存在しません");
        }else {
            task = taskOp.get();
            task.setActive(false);
            tasksRepository.save(task);
        }
        return "redirect:/tasks";
    }
    
    /**
     * ゴミ箱に入れたタスクを復活させる。
     * @param id 復活させるタスクのID
     * @return タスク一覧ページにリダイレクト
     */
    @PostMapping("/tasks/revive/{id}")
    public String revive(@PathVariable("id") Integer id){
        Optional<Tasks> taskOp = tasksRepository.findById(id);
        Tasks task = null;
        
        List<String> errList = new ArrayList<>();
        
        if(!taskOp.isPresent()) {
            errList.add("タスクが存在しません");
        }else {
            task = taskOp.get();
            task.setActive(true);
            tasksRepository.save(task);
        }
        return "redirect:/tasks";
    }
    
    /**
     * タスクを完全に削除する。
     * @param id 完全に削除するタスクのID
     * @return ゴミ箱ページにリダイレクト
     */
    @PostMapping("/tasks/destroy/{id}")
    public String destroy(@PathVariable("id") Integer id){
        tasksRepository.deleteById(id);
        return "redirect:/tasks?trash=true";
    }
    
    /**
     * 例外を捕捉し、ユーザーをログインページにリダイレクトする。
     * @param e 発生した例外
     * @param ra リダイレクト属性
     * @return ログインページへのリダイレクトURL
     */
    @ExceptionHandler(Exception.class)
    public String handleException(Exception e, RedirectAttributes ra) {
        // 例外の詳細をログに出力するなど、必要に応じて処理を追加
        e.printStackTrace();
        ra.addFlashAttribute("message", "予期せぬエラーが発生しました");
        return "redirect:/login";
    }
    
// ---------------------------- グラフデータ生成メソッド --------------------------------
    
    /**
     * 指定された月のタスクデータを集計する。
     * @param targetMonth 現在の月からの相対的な月数（0:今月, 1:翌月, -1:先月など）
     * @param progress 取得するタスクの進捗状況
     * @return 日付とタスク数のマップ
     */
    public Map<LocalDate, Integer> getGraphDataMonth(Integer targetMonth, Integer progress) {
        LocalDate now = LocalDate.now();
        LocalDate targetDate = (targetMonth != null && targetMonth != 0) ? now.plusMonths(targetMonth) : now;
        
        int length = targetDate.lengthOfMonth();
        Map<LocalDate, Integer> dbCountMap = new TreeMap<>();

        // ラベルの生成と初期化
        LocalDate currentDate = targetDate.withDayOfMonth(1);
        for (int i = 0; i < length; i++) {
            dbCountMap.put(currentDate.plusDays(i), 0);
        }
        
        List<Tasks> tasks;
        if (progress == -1) {
            tasks = tasksRepository.findByDateAreaNoneProgress(
                targetDate.withDayOfMonth(1), targetDate.withDayOfMonth(length), account.getId());
        } else {
            tasks = tasksRepository.findByDateArea(
                targetDate.withDayOfMonth(1), targetDate.withDayOfMonth(length), account.getId(), progress);
        }
        
        if (tasks.isEmpty()) {
            return dbCountMap;
        }
        
        for (LocalDate key : dbCountMap.keySet()) {
            Integer countClosingDate = tasksRepository.countByClosingDate(key, account.getId(), progress);
            if (countClosingDate != null && countClosingDate > 0) {
                dbCountMap.replace(key, countClosingDate);
            }
        }
        
        return dbCountMap;
    }
    
    /**
     * 指定された週のタスクデータを集計する。
     * @param targetWeek 現在の週からの相対的な週数（0:今週, 1:翌週, -1:先週など）
     * @param progress 取得するタスクの進捗状況
     * @return 日付とタスク数のマップ
     */
    public Map<LocalDate, Integer> getGraphDataWeek(Integer targetWeek, Integer progress) {
        LocalDate now = LocalDate.now();
        LocalDate targetDate = (targetWeek != null && targetWeek != 0) ? now.plusWeeks(targetWeek) : now;
        
        LocalDate monday = targetDate.with(DayOfWeek.MONDAY);
        LocalDate sunday = targetDate.with(DayOfWeek.SUNDAY);
        
        Map<LocalDate, Integer> dbCountMap = new TreeMap<>();

        // ラベルの生成と初期化
        for (int i = 0; i < 7; i++) {
            dbCountMap.put(monday.plusDays(i), 0);
        }
        
        List<Tasks> tasks = tasksRepository.findByDateArea(monday, sunday, account.getId(), progress);
        
        if (tasks.isEmpty()) {
            return dbCountMap;
        }
        
        for (LocalDate key : dbCountMap.keySet()) {
            Integer countClosingDate = tasksRepository.countByClosingDate(key, account.getId(), progress);
            if (countClosingDate != null && countClosingDate > 0) {
                dbCountMap.replace(key, countClosingDate);
            }
        }
        
        return dbCountMap;
    }
    
    
    
    /**
     * 指定された月の、特定のカテゴリに属するタスクデータを集計する。
     * @param targetMonth 現在の月からの相対的な月数
     * @param progress 取得するタスクの進捗状況
     * @param categoryId 絞り込むカテゴリID
     * @return 日付とタスク数のマップ
     */
    public Map<LocalDate, Integer> getGraphDataMonth(Integer targetMonth, Integer progress, Integer categoryId) {
        LocalDate now = LocalDate.now();
        LocalDate targetDate = (targetMonth != null && targetMonth != 0) ? now.plusMonths(targetMonth) : now;
        
        int length = targetDate.lengthOfMonth();
        Map<LocalDate, Integer> dbCountMap = new TreeMap<>();

        // ラベルの生成と初期化
        LocalDate currentDate = targetDate.withDayOfMonth(1);
        for (int i = 0; i < length; i++) {
            dbCountMap.put(currentDate.plusDays(i), 0);
        }
        
        List<Tasks> tasks;
        if (progress == -1) {
            tasks = tasksRepository.findByDateAreaNoneProgressAndCategoryId(
                targetDate.withDayOfMonth(1), targetDate.withDayOfMonth(length), account.getId(), categoryId);
        } else {
            tasks = tasksRepository.findByDateAreaAndCategoryId(
                targetDate.withDayOfMonth(1), targetDate.withDayOfMonth(length), account.getId(), progress, categoryId);
        }
        
        if (tasks.isEmpty()) {
            return dbCountMap;
        }
        
        for (LocalDate key : dbCountMap.keySet()) {
            Integer countClosingDate = tasksRepository.countByClosingDateAndCategoryId(key, account.getId(), progress, categoryId);
            if (countClosingDate != null && countClosingDate > 0) {
                dbCountMap.replace(key, countClosingDate);
            }
        }
        
        return dbCountMap;
    }
    
    /**
     * 指定された週の、特定のカテゴリに属するタスクデータを集計する。
     * @param targetWeek 現在の週からの相対的な週数
     * @param progress 取得するタスクの進捗状況
     * @param categoryId 絞り込むカテゴリID
     * @return 日付とタスク数のマップ
     */
    public Map<LocalDate, Integer> getGraphDataWeek(Integer targetWeek, Integer progress, Integer categoryId) {
        LocalDate now = LocalDate.now();
        LocalDate targetDate = (targetWeek != null && targetWeek != 0) ? now.plusWeeks(targetWeek) : now;
        
        LocalDate monday = targetDate.with(DayOfWeek.MONDAY);
        LocalDate sunday = targetDate.with(DayOfWeek.SUNDAY);
        
        Map<LocalDate, Integer> dbCountMap = new TreeMap<>();

        // ラベルの生成と初期化
        for (int i = 0; i < 7; i++) {
            dbCountMap.put(monday.plusDays(i), 0);
        }
        
        List<Tasks> tasks = tasksRepository.findByDateAreaAndCategoryId(monday, sunday, account.getId(), progress, categoryId);
        
        if (tasks.isEmpty()) {
            return dbCountMap;
        }
        
        for (LocalDate key : dbCountMap.keySet()) {
            Integer countClosingDate = tasksRepository.countByClosingDateAndCategoryId(key, account.getId(), progress, categoryId);
            if (countClosingDate != null && countClosingDate > 0) {
                dbCountMap.replace(key, countClosingDate);
            }
        }
        
        return dbCountMap;
    }
}
