package kr.co.itwill.product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
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
	
}//class end
