package kr.co.itwill.product;

import java.io.File;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/product")
public class ProductCont {

	public ProductCont() {
		System.out.println("-----ProductCont() 객체 생성됨");
	}//end
	
	@Autowired
	ProductDAO productDao;
		
	
	@RequestMapping("/list")
	public ModelAndView list() {
		ModelAndView mav=new ModelAndView();
		mav.setViewName("product/list");
		mav.addObject("list", productDao.list());
		return mav;
	}//list() end
	
	@RequestMapping("/write")
	public String write() {
		return "product/write";
	}//write() end
	
	@RequestMapping("/insert")
	public String insert(@RequestParam Map<String, Object> map
						,@RequestParam MultipartFile img
						,HttpServletRequest req) {
		
		//주의사항 : 파일업로드할때 리네임 되지 않음
		
		//업로드된 파일을 /storage폴더에 저장
		String filename="-";
		long filesize=0;	//테이블에 filesize칼럼 추가하면 됩니다
		if(img != null && !img.isEmpty()) {
			filename=img.getOriginalFilename();
			filesize=img.getSize();
			try {
				
				ServletContext application=req.getSession().getServletContext();
				String path=application.getRealPath("/storage");	//실제 물리적인 경로
				img.transferTo(new File(path+"\\"+filename));		//파일저장
				
			}catch (Exception e) {
				e.printStackTrace();	//System.out.println(e);
			}//try end
		}//if end
		
		map.put("filename", filename);
		map.put("filesize", filesize);
		productDao.insert(map);
		return "redirect:/product/list";
	}//insert() end
	
	@RequestMapping("/search")
	public ModelAndView search(@RequestParam(defaultValue = "") String product_name) {
		ModelAndView mav=new ModelAndView();
		mav.setViewName("product/list");
		mav.addObject("list", productDao.search(product_name));
		mav.addObject("product_name", product_name);
		return mav;
	}//search() end
	
	@RequestMapping("/detail/{product_code}")
	public ModelAndView detail(@PathVariable String product_code) {
		ModelAndView mav=new ModelAndView();
		mav.setViewName("product/detail");
		mav.addObject("product", productDao.detail(product_code));
		return mav;
	}//detail() end
	/*
		@RequestParam
		http://192.168.0.1:9090?aaa=bbb&ccc=ddd
		
		@PathVariable
		http://192.168.0.1:9090/bbb/ddd
	 */
	
	@RequestMapping("/update")
	public String update(@RequestParam Map<String, Object> map
						,@RequestParam MultipartFile img
						,HttpServletRequest req) {
		
		String filename="-";
		long filesize=0;
		if(img != null && !img.isEmpty()) {
			filename=img.getOriginalFilename();
			filesize=img.getSize();
			try {
				ServletContext application=req.getSession().getServletContext();
				String path=application.getRealPath("/storage");
				//System.out.println(path);
				//I:\java202207\workspace_spring\spring06_myshop\src\main\webapp\storage
				img.transferTo(new File(path + "\\" + filename));
			}catch (Exception e) {
				e.printStackTrace();
			}//try end
		}else {
			String product_code=map.get("product_code").toString();
			Map<String, Object> product=productDao.detail(product_code);
			filename=product.get("FILENAME").toString();
			filesize=(long)product.get("FILESIZE");
		}//if end
		
		map.put("filename", filename);
		map.put("filesize", filesize);
		productDao.update(map);
		return "redirect:/product/list";
	}//update() end
	
	@RequestMapping("/delete")
	public String delete(int product_code, HttpServletRequest req) {
		String filename=productDao.filename(product_code);	//파일삭제
		if(filename != null && !filename.equals("-")) {
			ServletContext application=req.getSession().getServletContext();
			String path=application.getRealPath("/storage");
			File file=new File(path + "\\" + filename);
			if(file.exists()) {
				file.delete();
			}//if end
		}//if end
		productDao.delete(product_code);	//행삭제
		return "redirect:/product/list";
	}//delete() end
	
	
}//class end
