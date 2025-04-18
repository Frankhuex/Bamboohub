import BookListByScope from "../components/BookListByScope";
export default function Plaza() {
    return (
        <>
            <br></br>
            <div className="tabs tabs-box mx-auto w-full flex justify-center lg:flex-none lg:justify-start" >
                <input type="radio" name="my_tabs_1" className="tab" aria-label="公开阅读" defaultChecked />
                <div className="tab-content border-base-300 bg-base-100 p-10">
                    <BookListByScope scope="allread" />
                    {/* <PlazaAllRead /> */}
                </div>
                
                <input type="radio" name="my_tabs_1" className="tab" aria-label="公开编辑" />
                <div className="tab-content border-base-300 bg-base-100 p-10">
                    <BookListByScope scope="alledit" />
                    {/* <PlazaAllEdit /> */}
                </div>
            </div>
        </>
    )
}