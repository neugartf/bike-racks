import UIKit
import domain

class ViewController: UIViewController {
    override func viewDidLoad() {
        super.viewDidLoad()
        
        GetBikeRacksUseCase(bikeRackRepository: BikeRackRepositoryImpl(overpassApi: ApiOverpassApi())).executeiOS(lat1: 0.0, lng1: 0.0, lat2: 0.0, lng2: 0.0, success: { data in
            data
        })
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }
    @IBOutlet weak var label: UILabel!
}
